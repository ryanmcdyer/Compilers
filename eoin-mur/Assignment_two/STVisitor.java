import java.util.*;

public class STVisitor implements BasicLVisitor
{

  //starting scope is program
  String scope = "Program";
  String prevScope;
  HashMap<String,HashMap<String,STC>> ST = new HashMap<String,HashMap<String,STC>>();
  int numErrors = 0;

  public Object visit(SimpleNode node, Object data)
  {
    node.childrenAccept(this, data);
    return null;
  }
  public Object visit(ASTProgram node, Object data)
  {
    //Add a new Node 
    ST.put(scope,new HashMap<String,STC>());

    //vistit all nodes in the program
    node.childrenAccept(this,data);

    
    System.out.println("--------Symbol Table---------\n");
    Set keys = ST.keySet();
    Iterator iter = keys.iterator();
    while(iter.hasNext())
    {
      String s = (String)iter.next();
      System.out.println("Scope: "+s);

      Set keys2 = ST.get(s).keySet();
      Iterator iter2 = keys2.iterator();
      while(iter2.hasNext())
      {
        String s2 = (String)iter2.next();
        STC temp = ST.get(s).get(s2);
        System.out.print("\tID: "+s2); 
        System.out.print("| DataType: "+temp.dType);
        System.out.print("| Type: "+temp.type.image);
        System.out.println("| Values: "+temp.values); 
      } 
    }

    if(numErrors > 0)
    {
      System.out.println("\nErrors: "+numErrors);
    }
    else
    {
      ThreeAddressVisitor tv = new ThreeAddressVisitor();
      node.jjtAccept(tv,ST);
    }


    return null;
  }

  //                Decl 
  //        /        |          \
  //     idList     Type  (idList   Type) *
  //     /
  //  id (id)*
  public Object visit(ASTDecl node, Object data)
  {
    int numChildren = node.jjtGetNumChildren();
    HashMap<String,STC> hTemp = ST.get(scope);
    if(hTemp == null)
    {
      hTemp = new HashMap<String,STC>();
    }

    //every decl jump 2 to get ot start of next
    for(int i = 0; i < numChildren; i=i+2)
    {
      List<Token> idList = (List<Token>)node.jjtGetChild(i).jjtAccept(this,null);
      Token type = (Token)node.jjtGetChild(i+1).jjtAccept(this,null);
      for(int j =0; j < idList.size(); j++)
      {
        if( hTemp.get(idList.get(j).image) == null)
        {
          STC sTemp = new STC( idList.get(j), type, scope,DataType.Var);
          hTemp.put(idList.get(j).image,sTemp);
        }
        //if it is already in the symbol table throw a error
        else
        {
          System.out.println("Identifier: "+idList.get(j).image+"\n\tAlready declared in scope: "+scope);
          System.out.println("Error Line: "+idList.get(j).beginLine+" Column: "+idList.get(j).beginColumn+"\n");
          numErrors++;
        }  
      }
      ST.put(scope,hTemp);
    }
   //System.out.println(ST.size());
     return null;
  }

  //         _______ConstDecl_______
  //        /        |     |        \
  //       Id       Type  Exp   (Id Type Exp)*
  //                     /   \
  //                    /     \
  //                   /       \
  //                Add         (Add)* //same as other side
  //               /   \        
  //            Mult (Mult)*  only results in node if a multOpp is found.. same for Add with a addOp     
  //              \     /
  //   (Id | Bool | Num | Real | Exp)

  public Object visit(ASTConstDecl node, Object data)
  {
    int numChildren = node.jjtGetNumChildren();
    HashMap<String,STC> hTemp = ST.get(scope);
    if(hTemp == null)
    {
      hTemp = new HashMap<String,STC>();
    }

    //for each Const jumping 3 to get to the start of next Const 
    for(int i = 0; i < numChildren;i=i+3)
    {
      Token name = (Token)node.jjtGetChild(0).jjtAccept(this,null);
      if(hTemp.get(name.image) == null)
      {
        Token type = (Token)node.jjtGetChild(i+1).jjtAccept(this,null);

        STC sTemp = new STC( name, type, scope, DataType.Const);
        //if the value is just a fragment
        if(node.jjtGetChild(i+2).jjtAccept(this,null) instanceof Token)
        {
          Token expTokens = (Token)node.jjtGetChild(i+2).jjtAccept(this,null);
          sTemp.addValue("fragment",expTokens);
        }
        //else if the value is an expression
        else
        {
          List<Token> expTokens = (List<Token>)node.jjtGetChild(i+2).jjtAccept(this,null);
          sTemp.addValue("expression",expTokens);
        }

        hTemp.put(name.image,sTemp);
      }
      //if it is already in the symbol table throw a error
      else
      {
        System.out.println("Identifier: "+name.image+"\n\tAlready declared in scope: "+scope);
        System.out.println("Error Line: "+name.beginLine+" Column: "+name.beginColumn+"\n");
        numErrors++;
      }  
    }
    ST.put(scope,hTemp);
    return null;
  }

  public Object visit(ASTFunction_Decl node, Object data)
  {

    HashMap<String,STC> hTemp = ST.get(scope);
    if(hTemp == null)
    {
      hTemp = new HashMap<String,STC>();
    }

    //put the function decl into the current scope 
    Token type = (Token)node.jjtGetChild(0).jjtAccept(this,null);
    Token name = (Token)node.jjtGetChild(1).jjtAccept(this,null);
    STC sTemp = new STC( name, type, scope, DataType.Function);
    
    hTemp.put(name.image,sTemp);
    ST.put(scope,hTemp);

    //change to the functions scope
    prevScope = scope;
    scope = name.image;

    //get the function param decl's
    node.jjtGetChild(2).jjtAccept(this,null);

    //go to the body
    node.jjtGetChild(3).jjtAccept(this,null);;

    scope = prevScope;
    return null;  
  }
  public Object visit(ASTFunction_body node, Object data)
  {
    node.childrenAccept(this,data);
    return null;
  }

  public Object visit(ASTParams node, Object data)
  {
    HashMap<String,STC> hTemp = ST.get(scope);
    if(hTemp == null)
    {
      hTemp = new HashMap<String,STC>();
    }

    //add param decl's to the functions scope
    int numChildren = node.jjtGetNumChildren();
    for(int i =0; i < numChildren; i=i+2)
    {
      Token name = (Token)node.jjtGetChild(i).jjtAccept(this,null);
      Token type = (Token)node.jjtGetChild(i+1).jjtAccept(this,null);
      STC temp = new STC(name,type,scope,DataType.ParamVar);

      hTemp.put(name.image,temp);
    }
    ST.put(scope,hTemp);

    return null;
  }
  public Object visit(ASTType node, Object data)
  {
    return node.jjtGetValue();
  }
  public Object visit(ASTMain node, Object data)
  {
    prevScope = scope;
    scope = "Main";

    node.childrenAccept(this,data);

    scope = prevScope;
    return null;
  }
  public Object visit(ASTAssign node, Object data)
  {
    int numChildren = node.jjtGetNumChildren();
    //get the left hand side type
    Token lhToken = (Token)node.jjtGetChild(0).jjtAccept(this,null);
    STC lhSTC = ST.get(scope).get(lhToken.image);
    if(lhSTC == null)
      lhSTC = ST.get("Program").get(lhToken.image);
    String lhType;
    //if the left hand side is not declared just through a error dont check the rest
    if(lhSTC != null)
    {
      if(lhSTC.dType == DataType.Var || lhSTC.dType == DataType.ParamVar)
      {
        lhType = lhSTC.type.image;
        for(int i = 1; i < numChildren; i++)
        {
          //if is a id
          if(node.jjtGetChild(i).jjtAccept(this,null) instanceof Token )
          {
            Token t = (Token)node.jjtGetChild(i).jjtAccept(this,null);
            //if the token is a ID and not in the symbol table for the global or current scope
            if(t.kind == 31 && (ST.get(scope).get(t.image) == null && ST.get("Program").get(t.image) == null))
            {
              System.out.println("Identifier: "+t.image+"\n\tNot declared in scope: "+scope+" Or Program");
              System.out.println("Error Line: "+t.beginLine+" Column: "+t.beginColumn+"\n");
              numErrors++;
            }
            //if not on the left hand side
            else
            {
              if(t.kind == 31)
              {
                STC rhSTC = ST.get(scope).get(t.image);
                if(rhSTC.type.image != lhType)
                {
                  System.out.println("Invalid type assign: \n\t"+lhToken.image+":"+lhType+" <- "+t.image+":"+rhSTC.type.image);
                  System.out.println("Error Line: "+t.beginLine+" Column: "+t.beginColumn+"\n");
                  numErrors++;
                }
              }
              //else we are dealing with numbers or bool values
              else
              {
                if(t.kind == 33 && lhType != "int")
                {
                  System.out.println("Invalid type assign: \n\t"+lhToken.image+":"+lhType+" <- "+t.image+":int");
                  System.out.println("Error Line: "+t.beginLine+" Column: "+t.beginColumn+"\n");
                  numErrors++;
                }
                else if((t.kind == 16 || t.kind == 25) && lhType != "bool")
                {
                  System.out.println("Invalid type assign: \n\t"+lhToken.image+":"+lhType+" <- "+t.image+":bool");
                  System.out.println("Error Line: "+t.beginLine+" Column: "+t.beginColumn+"\n");
                  numErrors++;
                }  
              }  
            }
          }
          else
          {
            List<Token> lT = (List<Token>)node.jjtGetChild(i).jjtAccept(this,null);
            for(int j = 0; j < lT.size(); j++)
            {
              Token t = (Token)lT.get(j);
              //if the token is a ID and not in the symbol table for the global or current scope
              if(t.kind == 31 && (ST.get(scope).get(t.image) == null && ST.get("Program").get(t.image) == null) )
              {
                System.out.println("Identifier: "+t.image+"\n\tNot declared in scope: "+scope+" Or Program");
                System.out.println("Error Line: "+t.beginLine+" Column: "+t.beginColumn+"\n");
                numErrors++;
              }
              else
              {
                if(t.kind == 31)
                {
                  STC rhSTC = ST.get(scope).get(t.image);
                  if(rhSTC.type.image != lhType)
                  {
                    System.out.println("Invalid type assign: \n\t"+lhToken.image+":"+lhType+" <- "+t.image+":"+rhSTC.type.image);
                    System.out.println("Error Line: "+t.beginLine+" Column: "+t.beginColumn+"\n");
                    numErrors++;
                  }
                }
                else
                {
                  if(t.kind == 33 && lhType != "int")
                  {
                    System.out.println("Invalid type assign: \n\t"+lhToken.image+":"+lhType+" <- "+t.image+":int");
                    System.out.println("Error Line: "+t.beginLine+" Column: "+t.beginColumn+"\n");
                    numErrors++;
                  }
                  else if((t.kind == 16 || t.kind == 25) && lhType != "bool")
                  {
                    System.out.println("Invalid type assign: \n\t"+lhToken.image+":"+lhType+" <- "+t.image+":bool");
                    System.out.println("Error Line: "+t.beginLine+" Column: "+t.beginColumn+"\n");
                    numErrors++;
                  }  
                }  
              }
            }
          }
        }     
      }
      else
      {
        System.out.println("Cannot Asign to constant: "+lhSTC.name.image);
        System.out.println("Error Line: "+lhSTC.name.beginLine+" Column: "+lhSTC.name.beginColumn+"\n");
        numErrors++;
      }  
    }
    else{
      System.out.println("Identifier: "+lhToken.image+"\n\tNot declared in scope: "+scope+" Or Program");
      System.out.println("Error Line: "+lhToken.beginLine+" Column: "+lhToken.beginColumn+"\n");
      numErrors++;
    }
    return null;
  }

  public Object visit(ASTAdd node, Object data)
  {
    List<Token> al = new ArrayList();
    for(int i = 0; i < node.jjtGetNumChildren();i++)
    {
      if(node.jjtGetChild(i).jjtAccept(this,null) instanceof Token)
        al.add((Token)node.jjtGetChild(i).jjtAccept(this,null));
      else
      {
        List<Token> lT = (List<Token>)node.jjtGetChild(i).jjtAccept(this,null);
        for(int j = 0; j < lT.size(); j++)
        {
          al.add((Token)lT.get(j));
        }
      }
    }
    return al;
  }
  public Object visit(ASTMult node, Object data)
  {
    List<Token> al = new ArrayList();
    for(int i = 0; i < node.jjtGetNumChildren();i++)
    {
      if(node.jjtGetChild(i).jjtAccept(this,null) instanceof Token)
        al.add((Token)node.jjtGetChild(i).jjtAccept(this,null));
      else
      {
        List<Token> lT = (List<Token>)node.jjtGetChild(i).jjtAccept(this,null);
        for(int j = 0; j < lT.size(); j++)
        {
          al.add((Token)lT.get(j));
        }
      }
    }
    return al;
  }
  public Object visit(ASTAddOp node, Object data)
  {
    return node.jjtGetValue();
  }
  public Object visit(ASTMultOp node, Object data)
  {
    return node.jjtGetValue();
  }
  public Object visit(ASTBoolOpp node, Object data)
  {
    return node.jjtGetValue();
  }

  public int checkConditionIds(ASTCondition node, Object data)
  {
    int errorsFound = -1;
    int numChildren = node.jjtGetNumChildren();
    for(int i = 0; i < numChildren; i=i+2) 
    {
      //if is a id
      if(node.jjtGetChild(i).jjtAccept(this,null) instanceof Token )
      {
        Token t = (Token)node.jjtGetChild(i).jjtAccept(this,null);
        //if the token is a ID and not in the symbol table for the global or current scope
        if(t.kind == 31 && (ST.get(scope).get(t.image) == null && ST.get("Program").get(t.image) == null))
        {
          System.out.println("identifier: "+t.image+"\n\tnot declared in scope: "+scope+" Or Program");
          System.out.println("Error Line: "+t.beginLine+" Column: "+t.beginColumn+"\n");
          numErrors++;
          errorsFound = 1;
        }
      }
      else
      {
        List<Token> lT = (List<Token>)node.jjtGetChild(i).jjtAccept(this,null);
        for(int j = 0; j < lT.size(); j++)
        {
          Token t = (Token)lT.get(j);
          //if the token is a ID and not in the symbol table for the global or current scope
          if(t.kind == 31 && (ST.get(scope).get(t.image) == null && ST.get("Program").get(t.image) == null) )
          {
            System.out.println("identifier: "+t.image+"\n\tnot declared in scope: "+scope+" Or Program");
            System.out.println("Error Line: "+t.beginLine+" Column: "+t.beginColumn+"\n");
            numErrors++;
            errorsFound = 1;
          }
        }
      }  
    }
    return errorsFound;
  }

  public Object visit(ASTCondition node, Object data)
  {
    //if all the Identifers are declared check if they can reach a boolean condition
    if(checkConditionIds(node,null) == -1)
    {
      int numChildren = node.jjtGetNumChildren();
      //if there is only one just check if it is a boolean identifer
      if(numChildren == 1)
      {
        Token t = null;
        if(node.jjtGetChild(0).jjtAccept(this,null) instanceof Token)
          t = (Token)node.jjtGetChild(0).jjtAccept(this,null);
        //if its a function it is returned as a list of tokens so we need to get the function id at the start
        else
          t = ((List<Token>)node.jjtGetChild(0).jjtAccept(this,null)).get(0);
        //if not in current scope its in the global (we no its declared from previous check)
        STC stc = ST.get(scope).get(t.image);
        if(stc == null)
          stc = ST.get("Program").get(t.image);

        if(stc.type.image != "bool")
        {
          System.out.println("Unreachable Condition: "+t.image+"\n\t Not a Boolean Identifier");
          System.out.println("Error Line: "+t.beginLine+" Column: "+t.beginColumn+"\n");
          numErrors++;
        }
      }
    }
    return null;
  }

  public Object visit(ASTIdent_list node, Object data)
  {
    int numChildren = node.jjtGetNumChildren();
    List<Token> al = new ArrayList();
    for(int i = 0; i < numChildren; i++)
    {
      al.add((Token)((SimpleNode)node.jjtGetChild(i)).jjtGetValue());
    }
    return al;
  }

  public Object visit(ASTArg_list node, Object data)
  {
    int numChildren = node.jjtGetNumChildren();
    List<Token> al = new ArrayList();
    for(int i = 0; i < numChildren; i++)
    {
      al.add((Token)((SimpleNode)node.jjtGetChild(i)).jjtGetValue());
    }
    return al;
  }
  public Object visit(ASTId node, Object data)
  {
    return node.jjtGetValue();
  }
  public Object visit(ASTNum node, Object data)
  {
    return node.jjtGetValue();
  }
  public Object visit(ASTBool node, Object data)
  {
    return node.jjtGetValue();
  }
  public Object visit(ASTReal node, Object data)
  {
    return node.jjtGetValue();
  }

  public void checkFunctionCall(ASTFunctionCall node, Object data)
  {
    //get the number of params the function has in the symbol table
    int numParams = 0;
    Token t = (Token)node.jjtGetChild(0).jjtAccept(this,null);
    HashMap<String,STC> hTemp = ST.get(t.image);
    if(hTemp != null)
    {
      Set keys = hTemp.keySet();
      Iterator iter = keys.iterator();
      while(iter.hasNext())
      { 
        if(hTemp.get(iter.next()).dType == DataType.ParamVar)
          numParams ++;
      }
      int paramsPassed = ((List<Token>)node.jjtGetChild(1).jjtAccept(this,null)).size();
      if((paramsPassed) != numParams)
      {
        System.out.println("Call to Functon: "+t.image+"\n\tInvalid number of paramaters");
        System.out.println("Error Line: "+t.beginLine+" Column: "+t.beginColumn+"\n");
        numErrors++;
      }
    }
  }

  public Object visit(ASTFunctionCall node, Object data)
  {
    checkFunctionCall(node,null);

    List<Token> al = new ArrayList();
    for(int i = 0; i < node.jjtGetNumChildren();i++)
    {
      if(node.jjtGetChild(i).jjtAccept(this,null) instanceof Token)
        al.add((Token)node.jjtGetChild(i).jjtAccept(this,null));
      else
      {
        List<Token> lT = (List<Token>)node.jjtGetChild(i).jjtAccept(this,null);
        for(int j = 0; j < lT.size(); j++)
        {
          al.add((Token)lT.get(j));
        }
      }
    }
    return al;
  }
}