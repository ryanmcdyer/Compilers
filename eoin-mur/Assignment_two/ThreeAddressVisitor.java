import java.util.*;

public class ThreeAddressVisitor implements BasicLVisitor
{

  /*
    Address instructions:
    x = y
    x = y op z
    x = op y
    goto L
    if x goto L
    ifFalse x goto L
    if x relop y goto L
    param xn
    y = call p,n
    return

  */

  String label = "L1";
  String prevLabel;
  int curTempCount = 0;
  int paramCount = 0;
  int labelCount = 1;
  HashMap<String,Vector<Quadruple>> addrCode = new HashMap<String,Vector<Quadruple>>();
  HashMap<String,String> jumpLabelMap = new HashMap<String,String>();

	public Object visit(SimpleNode node, Object data)
	{
		node.childrenAccept(this, data);
    return null;
	}
  public Object visit(ASTProgram node, Object data)
  {
    System.out.println("\n-------IR 3-address code-------\n");
    node.childrenAccept(this,data);
    Set keys = addrCode.keySet();
    Iterator iter = keys.iterator();
    int count = 1;
    while(iter.hasNext())
    {
      String s = (String)iter.next();
      Vector<Quadruple> v = (Vector<Quadruple>)addrCode.get(s);
      System.out.println(s);
      for(int i =0; i < v.size(); i++)
      {
        v.get(i).printQuad();
      }
    } 
  	return null;
  }
  public Object visit(ASTDecl node, Object data)
  {
    return null;
  }
  public Object visit(ASTConstDecl node, Object data)
  {
    Vector<Quadruple> temp = addrCode.get(label);
    if(temp == null)
      temp = new Vector<Quadruple>();
    int numChildren = node.jjtGetNumChildren();
    for(int i = 0; i < numChildren; i=i+3)
    {
      Quadruple decl = new Quadruple(
          "=", 
          (String)node.jjtGetChild(i).jjtAccept(this,null), 
          (String)node.jjtGetChild(i+2).jjtAccept(this,null)
        );
      temp.add(decl);
    }
    addrCode.put(label,temp);
  	return null;
  }
  public Object visit(ASTFunction_Decl node, Object data)
  {
    prevLabel = label;
    label = "L"+(labelCount+1);

    //add the lable to the map so we can get funcions label when using a goto function
    jumpLabelMap.put((String)node.jjtGetChild(1).jjtAccept(this,null) , label);

    node.childrenAccept(this,data);

    Vector<Quadruple> temp = addrCode.get(label);
    if(temp == null)
      temp = new Vector<Quadruple>();

    Quadruple retrun = new Quadruple(
        "return", 
        ""
      );
    temp.add(retrun);
    addrCode.put(label,temp);

    label = prevLabel;
    labelCount++;

  	return null;
  }
  public Object visit(ASTFunction_body node, Object data)
  {
    node.childrenAccept(this,data);

  	return null;
  }
  public Object visit(ASTParams node, Object data)
  {
  	return null;
  }
  public Object visit(ASTType node, Object data)
  {
  	return null;
  }
  public Object visit(ASTMain node, Object data)
  {
    prevLabel = label;
    label = "L"+(labelCount+1);

    node.childrenAccept(this,data);

    label = prevLabel;
    labelCount++;
  	return null;
  }
  /*
         Assign
    /             \
   ID         (Exp | FunctionCall)
              / 
             Add   
          /   |   \
      Frag  (addOp  Frag)*

  */

  public Object visit(ASTAssign node, Object data)
  {
    Vector<Quadruple> temp = addrCode.get(label);
    if(temp == null)
      temp = new Vector<Quadruple>();
    //assign is just a copy instuction generate any nessacry temp vars mult and add
    Quadruple decl = new Quadruple(
        "=", 
        (String)node.jjtGetChild(0).jjtAccept(this,null),
        (String)node.jjtGetChild(1).jjtAccept(this,null)
      );
    temp.add(decl);
    addrCode.put(label,temp);
    return null;

  }
  
  public Object visit(ASTAdd node, Object data)
  {
    Vector<Quadruple> temp = addrCode.get(label);
    if(temp == null)
      temp = new Vector<Quadruple>();

    curTempCount++;
    String tempName = "t"+curTempCount;
    Quadruple add = new Quadruple(
        (String)node.jjtGetChild(1).jjtAccept(this,null),
        tempName,
        (String)node.jjtGetChild(0).jjtAccept(this,null),
        (String)node.jjtGetChild(2).jjtAccept(this,null)
      );  
    temp.add(add); 
    addrCode.put(label,temp);
  	return tempName;
  }

  public Object visit(ASTMult node, Object data)
  {
    Vector<Quadruple> temp = addrCode.get(label);
    if(temp == null)
      temp = new Vector<Quadruple>();

  	curTempCount++;
    String tempName = "t"+curTempCount;
    Quadruple mult = new Quadruple(
        (String)node.jjtGetChild(1).jjtAccept(this,null),
        tempName,
        (String)node.jjtGetChild(0).jjtAccept(this,null),
        (String)node.jjtGetChild(2).jjtAccept(this,null)
      );  
    temp.add(mult);
    addrCode.put(label,temp);
    return tempName;
  }
  public Object visit(ASTBoolOpp node, Object data)
  {
  	return null;
  }
  public Object visit(ASTAddOp node, Object data)
  {
    return ((Token)node.jjtGetValue()).image;
  }
  public Object visit(ASTMultOp node, Object data)
  {
    return ((Token)node.jjtGetValue()).image;
  }
  public Object visit(ASTCondition node, Object data)
  {
    node.childrenAccept(this,data);
  	return null;
  }
  public Object visit(ASTIdent_list node, Object data)
  {
  	return null;
  }
  public Object visit(ASTArg_list node, Object data)
  {
    Vector<Quadruple> temp = addrCode.get(label);
    if(temp == null)
      temp = new Vector<Quadruple>();

    paramCount++;
    String paramSetName = "param"+paramCount;
    for(int i = 0; i < node.jjtGetNumChildren(); i++)
    {
      Quadruple param = new Quadruple(
          paramSetName,
          (String)node.jjtGetChild(i).jjtAccept(this,null)  
        );  
      temp.add(param);
    }
    addrCode.put(label,temp);
    return paramSetName;
  }
  public Object visit(ASTId node, Object data)
  {
  	return ((Token)node.jjtGetValue()).image;
  }
  public Object visit(ASTNum node, Object data)
  {
  	return ((Token)node.jjtGetValue()).image;
  }
  public Object visit(ASTBool node, Object data)
  {
  	return ((Token)node.jjtGetValue()).image;
  }
  public Object visit(ASTReal node, Object data)
  {
  	return ((Token)node.jjtGetValue()).image;
  }
  public Object visit(ASTFunctionCall node, Object data)
  {
    Vector<Quadruple> temp = addrCode.get(label);
    if(temp == null)
      temp = new Vector<Quadruple>();

    Quadruple call = new Quadruple(
        "call",
        "",
        (String)node.jjtGetChild(0).jjtAccept(this,null),
        (String)node.jjtGetChild(1).jjtAccept(this,null)
      ); 

    temp.add(call);

    Quadruple gt = new Quadruple(
        "goto",
        jumpLabelMap.get((String)node.jjtGetChild(0).jjtAccept(this,null)),
        ""
      );
    temp.add(gt);

    addrCode.put(label,temp);
  	return null;
  }
}