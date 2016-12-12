import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

//Ryan McDyer
//13431038

public class SCVisitor implements CCALVisitor {

  private HashMap<String, HashMap<String, SymbolTable>> symbolTable = new HashMap<>();

  private static HashSet<String> declaredFunctions = new HashSet<String>();
  private static HashSet<String> calledFunctions = new HashSet<String>();

  private static final String GLOBAL_SCOPE = "Global";
  private static Stack<String> scopes = new Stack<>();

	public HashMap<String, HashMap<String, SymbolTable>> getSymbolTable() {
		return symbolTable;
	}

  public Object visit(SimpleNode node, Object data) {
    return null;
  }

  public Object visit(ASTProgram node, Object data) {
    if(scopes.empty()) {
      scopes.push(GLOBAL_SCOPE);
    }
		symbolTable.put(scopes.peek(), new HashMap<String, SymbolTable>());
		node.childrenAccept(this, data);

    if(declaredFunctions.size() == 0) {
      System.out.println("There were no functions declared");
    } else if(declaredFunctions.size() == calledFunctions.size()) {
			System.out.println("All " + declaredFunctions.size() + " function(s) were called.");
		} else {
      Set<String> diff = declaredFunctions;
      diff.removeAll(calledFunctions);
			System.out.println(diff.size() + " of " + declaredFunctions.size() + " declared functions were not called.\nFunctions not called are: ");
      for(String s : diff) {
        System.out.println("--> " + s);
      }
		}

    System.out.println("\n********** Symbol Table **********");
		for(String scope : symbolTable.keySet()) {
			HashMap<String, SymbolTable> scopedST = symbolTable.get(scope);
			System.out.println("Table for Scope: " + scope);
      System.out.println("\tID\tToken\tType\t\tValue(s)");
			for(String s : scopedST.keySet()) {
				SymbolTable st = scopedST.get(s);
				System.out.print("\t" + st.getID());
        System.out.print("\t" + st.getToken());
        System.out.print("\t" + st.getDataType());
        System.out.println("\t" + st.getValues());
			}
			System.out.println();
		}
		return null;
  }

  public Object visit(ASTVar_decl node, Object data) {

    //increment in 2s because each Variable decl has 2 elements
    for(int i = 0; i < node.jjtGetNumChildren(); i = i + 2) {
      Token id = (Token) node.jjtGetChild(i).jjtAccept(this, data);
      Token type = (Token) node.jjtGetChild(i+1).jjtAccept(this, data);

      HashMap<String, SymbolTable> scopedST = symbolTable.get(scopes.peek());
      if(scopedST == null) {
        scopedST = new HashMap<>();
      }

      SymbolTable tmpST = new SymbolTable(id, type, scopes.peek(), DataType.Variable);

      if(scopedST.get(id.image) == null) {
        //System.out.println("Adding new var " + id.image + " in scope \"" + scopes.peek() + "\"");
        scopedST.put(id.image, tmpST);
        symbolTable.put(scopes.peek(), scopedST);
      } else {
        System.out.println("Error: " + id.image + " was already declared in " + scopes.peek());
      }

    }
    return null;
  }

  public Object visit(ASTConst_decl node, Object data) {

    HashMap<String, SymbolTable> scopedST = symbolTable.get(scopes.peek());
    if(scopedST == null) {
      scopedST = new HashMap<>();
    }

    //increment in 3s because each Constant decl has 3 elements
    for(int i = 0; i < node.jjtGetNumChildren(); i = i + 3) {
      SimpleNode convertedNode = (SimpleNode) node.jjtGetChild(i);
      Token id = (Token) convertedNode.jjtGetValue();
      Token type = (Token) node.jjtGetChild(i + 1).jjtAccept(this, data);
      List<Token> list;

      if(node.jjtGetChild(i+2).jjtAccept(this, data) instanceof Token) {
        list = new ArrayList<>();
        list.add((Token) node.jjtGetChild(i+2).jjtAccept(this, data));
      } else {
        list = Arrays.asList((Token) node.jjtGetChild(i+2).jjtAccept(this, data));
      }

      SymbolTable tmpST = new SymbolTable(id, type, scopes.peek(), DataType.Constant);
      tmpST.add("value", list);

      HashMap<String, SymbolTable> globalST = symbolTable.get(GLOBAL_SCOPE);
      //if(globalST.get(id.image) != null) {
      if(globalST.containsKey(id.image)) {
        System.out.println("Error: Constant " + id.image + " already declared in " + GLOBAL_SCOPE);
      } else if(scopedST.get(id.image) == null) {
        scopedST.put(id.image, tmpST);
        symbolTable.put(scopes.peek(), scopedST);
      } else {
        System.out.println("Error: " + id.image + " was already declared in " + scopes.peek());
      }
    }
    return null;
  }

  public Object visit(ASTFunctionList node, Object data) {
    node.childrenAccept(this, data);
    return null;
  }

  public Object visit(ASTFunc_decl node, Object data) {
		Token type = (Token) node.jjtGetChild(0).jjtAccept(this, data);
		SimpleNode convertedNode = (SimpleNode) node.jjtGetChild(1);
		Token id = (Token) convertedNode.jjtGetValue();
    List<Token> paramsList;

    SymbolTable tmpST = new SymbolTable(id, type, scopes.peek(), DataType.Function);

    paramsList = (List<Token>) node.jjtGetChild(2).jjtAccept(this, data);
    tmpST.add("parameters", paramsList);

		HashMap<String, SymbolTable> globalST = symbolTable.get(GLOBAL_SCOPE);

    if(globalST.get(id.image) == null) {
      globalST.put(id.image, tmpST);
      symbolTable.put(GLOBAL_SCOPE, globalST);
    } else {
      System.out.println(id.image + " already declared in " + GLOBAL_SCOPE);
    }

		scopes.push("Function: " + id.image);

    //Increment i by 2 because each param has 2 parts
    for(int i = 0; i < paramsList.size(); i = i + 2) {
			Token paramId = paramsList.get(i);
			Token paramType = paramsList.get(i + 1);
			SymbolTable funcVar = new SymbolTable(paramId, paramType, scopes.peek(), DataType.Variable);

			HashMap<String, SymbolTable> funcST = symbolTable.get(scopes.peek());
			if(funcST == null) {
        funcST = new HashMap<String, SymbolTable>();
      }

			if(globalST.get(paramId.image) != null) {
				System.out.println("Error: Parameter " + paramId.image + " of type " + paramType.image + " already in scope " + GLOBAL_SCOPE);
			}	else if(funcST.get(paramId.image) == null) {
				funcST.put(paramId.image, funcVar);
				symbolTable.put(scopes.peek(), funcST);
			} else {
				System.out.println("Parameter " + paramId.image + " of type " + paramType.image + " already in scope " + scopes.peek());
			}
		}

		Token funcBody = (Token) node.jjtGetChild(3).jjtAccept(this, data);
    declaredFunctions.add(id.image);
    scopes.pop();

    return null;
  }

  public Object visit(ASTFunc_body node, Object data) {
    node.childrenAccept(this, data);
		return null;
  }

  public Object visit(ASTType node, Object data) {
		return node.jjtGetValue();
  }

  public Object visit(ASTParamList node, Object data) {
		List<Token> params = new ArrayList<Token>();
		for(int i = 0; i < node.jjtGetNumChildren(); i++) {
      SimpleNode tmpNode = (SimpleNode) node.jjtGetChild(i);
			params.add((Token) tmpNode.jjtGetValue());
		}
		return params;
  }

  public Object visit(ASTMain node, Object data) {
    scopes.push("Function: Main");
    symbolTable.put(scopes.peek(), new HashMap<String, SymbolTable>());
		node.childrenAccept(this, data);
    scopes.pop();
    return null;
  }

  public Object visit(ASTIfCond node, Object data) {
		node.childrenAccept(this, data);
    return null;
  }

  public Object visit(ASTWhileCond node, Object data) {
		node.childrenAccept(this, data);
    return null;
  }

  public Object visit(ASTAssignment node, Object data) {
    String s = "assignment";
    Token id = (Token) node.jjtGetChild(0).jjtAccept(this, (Object) s);

		HashMap<String, SymbolTable> scopedST = symbolTable.get(scopes.peek());

		SymbolTable idInCurrentScope = scopedST.get(id.image);
		if(idInCurrentScope == null) {
			System.out.println("Error: Variable " + id.image + " in " + scopes.peek() + " not declared before assignment.");
		}

		List<Token> tokens = null;

		Object remainder = node.jjtGetChild(1).jjtAccept(this, data);

		if(remainder instanceof Token) {
			tokens = new ArrayList<Token>();
			tokens.add((Token) remainder);
		} else {
			tokens = (List<Token>) remainder;
		}

		if(idInCurrentScope != null) {
			if(idInCurrentScope.getDataType() == DataType.Constant) {
				System.out.println("Error: Constant " + id.image + " already has a value.");
			} else {
				idInCurrentScope.add("written", true);
				idInCurrentScope.add("value", tokens);
			}
		}
		return null;
  }

  public Object visit(ASTFunctionCall node, Object data) {
    Token id = (Token) node.jjtGetChild(0).jjtAccept(this, data);
		List<Token> suppliedParams = (List<Token>) node.jjtGetChild(1).jjtAccept(this, data);

		HashMap<String, SymbolTable> globalST = symbolTable.get(GLOBAL_SCOPE);
		SymbolTable funcST = globalST.get(id.image);

		HashMap<String, SymbolTable> scopedST = symbolTable.get(scopes.peek());

    if(funcST != null) {
  		if(funcST.getDataType() == DataType.Function) {
  			calledFunctions.add(id.image);

  			if(funcST.get("parameters") != null) {
  				List<Token> requiredParams = (List<Token>) funcST.get("parameters");
          //Div by 2 because each param is stored in 2 parts
  				if(suppliedParams.size() != requiredParams.size()/2) {
  					System.out.println("Error: Incorrect number of params for " + id.image + " in " + scopes.peek());
  				}
          //Code for checking if the params are of correct type, but it doesn't work
          /*else {
            SymbolTable st = null;
            for(int i = 0; i < requiredParams.size(); i = i+2) {
              System.out.println("Required param type: " + requiredParams.get(i+1));
              st = scopedST.get(suppliedParams.get(i/2));
              System.out.println("Supplied param type: " + st.getToken());
            }
          }*/
  			}
  		} else {
        System.out.println("Error: " + id.image + " is not a function");
      }
    } else {
      System.out.println("Error: The function " + id.image + " doesn't exist");
    }
		return null;
  }

  public Object visit(ASTAddOperation node, Object data) {
		checkDataType(node.jjtGetChild(0).jjtAccept(this, data));
		checkDataType(node.jjtGetChild(1).jjtAccept(this, data));
		return Arrays.asList(node.jjtGetChild(0).jjtAccept(this, data), node.jjtGetChild(1).jjtAccept(this, data));
  }

  public Object visit(ASTSubOperation node, Object data) {
		checkDataType(node.jjtGetChild(0).jjtAccept(this, data));
		checkDataType(node.jjtGetChild(1).jjtAccept(this, data));
		return Arrays.asList(node.jjtGetChild(0).jjtAccept(this, data), node.jjtGetChild(1).jjtAccept(this, data));
  }

  public void checkDataType(Object o) {
    if(!(o instanceof Token)) {
      return;
    }
		Token id = (Token) o;

		HashMap<String, SymbolTable> globalST = symbolTable.get(GLOBAL_SCOPE);
		HashMap<String, SymbolTable> scopedST = symbolTable.get(scopes.peek());

		SymbolTable st = null;

		if(globalST.get(id.image) != null) {
			st = globalST.get(id.image);
		} else if(scopedST.get(id.image) != null) {
			st = scopedST.get(id.image);
		}

		if(st != null) {
			if(st.getDataType() == DataType.Variable) {
				System.out.print(st.getID().image + " has DataType Variable, and has type ");
			} else if(st.getDataType() == DataType.Constant) {
				System.out.print(st.getID().image + " has DataType Constant, and has type ");
			}
      System.out.println(st.getToken());
		}
  }

  public Object visit(ASTId node, Object data) {
    String s = (String) data;

    //Special case for if this is an assignment to a var/const
    if(s != null && s.equals("assignment")) {
      return node.jjtGetValue();
    }
    Token token = (Token) node.jjtGetValue();
		HashMap<String, SymbolTable> scopedST = symbolTable.get(scopes.peek());

		SymbolTable idInCurrentScope = scopedST.get(token.image);

		if(scopedST != null) {
      if(idInCurrentScope == null && !declaredFunctions.contains(token.image)) {
  			System.out.println("Error: " + token.image + " not declared in scope " + scopes.peek() + " before use.");
      } else if(idInCurrentScope != null) {
  			idInCurrentScope.add("read", true);
  			scopedST.put(token.image, idInCurrentScope);
  		}
    }
		return node.jjtGetValue();
  }

  public Object visit(ASTNumber node, Object data) {
    return node.jjtGetValue();
  }

  public Object visit(ASTBoolean node, Object data) {
    Token token = (Token) node.jjtGetValue();
		return token;
  }

  public Object visit(ASTLogic_Op node, Object data) {
    return node.jjtGetValue();
  }

  public Object visit(ASTCompare_Op node, Object data) {
    return node.jjtGetValue();
  }

  public Object visit(ASTArgList node, Object data) {
		List<Token> args = new ArrayList<Token>();
		for(int i = 0; i < node.jjtGetNumChildren(); i++) {
			args.add((Token) node.jjtGetChild(i).jjtAccept(this, data));
		}
		return args;
	}
}
