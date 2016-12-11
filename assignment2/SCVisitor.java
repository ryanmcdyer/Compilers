import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

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

/*      System.out.println(declaredFunctions.size());
    System.out.println(node.jjtGetNumChildren());
    System.out.println(node.jjtGetChild(0));
    System.out.println(calledFunctions.size());*/

    if(declaredFunctions.size() == 0) {
      System.out.println("There were no functions declared");
    }
		else if(declaredFunctions.size() == calledFunctions.size()) {
			System.out.println("All " + declaredFunctions.size() + " functions were called.");
		} else {
      Set<String> diff = declaredFunctions;
      diff.removeAll(calledFunctions);
			System.out.println(diff.size() + " of " + declaredFunctions.size() + " declared functions were not called.\nFunctions not called are: ");
      for(String s : diff) {
        System.out.println("  * " + s);
      }
		}

    System.out.println("\n**********Symbol Table");
		for (String scope : symbolTable.keySet()) {
			HashMap<String, SymbolTable> scopedST = symbolTable.get(scope);
			System.out.println("Scope: " + scope);

			for (String id : scopedST.keySet()) {
				SymbolTable st = scopedST.get(id);
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
    /*System.out.println(node + " with " + node.jjtGetNumChildren() + " children");
    System.out.println("value: " + " " + node.jjtGetValue());
    System.out.println(node.jjtGetChild(0)); //Id
    System.out.println(node.jjtGetChild(1)); //Type*/

    for (int i = 0; i < node.jjtGetNumChildren(); i = i + 2) {
      Token id = (Token) node.jjtGetChild(i).jjtAccept(this, data);
      Token type = (Token) node.jjtGetChild(i+1).jjtAccept(this, data);

      HashMap<String, SymbolTable> scopedST = symbolTable.get(scopes.peek());
      if(scopedST == null) {
        scopedST = new HashMap<>();
      }

      SymbolTable tmpST = new SymbolTable(id, type, scopes.peek(), DataType.Variable);

      if(scopedST.get(id.image) == null) {
        System.out.println("Adding new var " + id.image + " in scope \"" + scopes.peek() + "\"");
        scopedST.put(id.image, tmpST);
        symbolTable.put(scopes.peek(), scopedST);
      } else {
        System.out.println("Error: " + id.image + " was already declared in " + scopes.peek());
      }
      System.out.println(i);

    }
    return null;
  }

  public Object visit(ASTConst_decl node, Object data) {
    /*System.out.println(node + " with " + node.jjtGetNumChildren() + " children");
    System.out.println("value: " + " " + node.jjtGetValue());
    System.out.println(node.jjtGetChild(0)); //Id
    System.out.println(node.jjtGetChild(1)); //Type
    System.out.println(node.jjtGetChild(2)); //Fragment*/

    HashMap<String, SymbolTable> scopedST = symbolTable.get(scopes.peek());
    if(scopedST == null) {
      scopedST = new HashMap<>();
    }

    for (int i = 0; i < node.jjtGetNumChildren(); i += 3) {
      SimpleNode convertedNode = (SimpleNode) node.jjtGetChild(i);
      Token identifer = (Token) convertedNode.jjtGetValue();
      Token type = (Token) node.jjtGetChild(i + 1).jjtAccept(this, data);
      List<Token> val;

      if(node.jjtGetChild(i+2).jjtAccept(this, data) instanceof Token) {
        val = new ArrayList<>();
        val.add((Token) node.jjtGetChild(i+2).jjtAccept(this, data));
      }
      else {
        val = Arrays.asList((Token) node.jjtGetChild(i+2).jjtAccept(this, data));
      }

      SymbolTable tmpST = new SymbolTable(identifer, type, scopes.peek(), DataType.Constant);
      tmpST.add("value", val);

      HashMap<String, SymbolTable> globalScopeSymbolTable = symbolTable.get(GLOBAL_SCOPE);
      //if(globalScopeSymbolTable.get(identifer.image) != null) {
      if(globalScopeSymbolTable.containsKey(identifer.image)) {
        System.out.println("Error: Constant " + identifer.image + " already declared in " + GLOBAL_SCOPE);
      } else if(scopedST.get(identifer.image) == null) {
        scopedST.put(identifer.image, tmpST);
        symbolTable.put(scopes.peek(), scopedST);
      } else {
        System.out.println("Error: " + identifer.image + " was already declared in " + scopes.peek());
      }
    }
    return null;
  }

  public Object visit(ASTFunctionList node, Object data) {
    node.childrenAccept(this, data);
    return null;
  }

  public Object visit(ASTFunc_decl node, Object data) {
    System.out.println();
    System.out.println("Function Decl, numKids " + node.jjtGetNumChildren());
    for(int i = 0; i < node.jjtGetNumChildren(); i++) {
      System.out.println(i + " " + node.jjtGetChild(i));
    }

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
      System.out.println(id.image + " already declared in " + GLOBAL_SCOPE); //TODO: Maybe not currentScope ?
    }

		scopes.push("Function: " + id.image);

    for (int i = 0; i < paramsList.size(); i = i + 2) {
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
		for (int i = 0; i < node.jjtGetNumChildren(); i++) {
      SimpleNode tmpNode = (SimpleNode) node.jjtGetChild(i);
			params.add((Token) tmpNode.jjtGetValue());
		}
		return params;
  }

  public Object visit(ASTMain node, Object data) {
    scopes.push("Function: Main");
		node.childrenAccept(this, data);
    scopes.pop();
    return null;
  }

/*  public Object visit(ASTStatement node, Object data) {
    return null;
  }

  public Object visit(ASTExpr node, Object data) {
    System.out.println("EXPRESSION*****************");
    SimpleNode n = (SimpleNode) node.jjtGetChild(0);
    System.out.println((Token) n.jjtGetValue());
    return null;
  }*/

  public Object visit(ASTIfCond node, Object data) {
		node.childrenAccept(this, data);
    return null;
  }

  public Object visit(ASTWhileCond node, Object data) {
		node.childrenAccept(this, data);
    return null;
  }

  public Object visit(ASTAssignment node, Object data) {
    Token assignedId = (Token) node.jjtGetChild(0).jjtAccept(this, data);

		HashMap<String, SymbolTable> scopedST = symbolTable.get(scopes.peek());
		HashMap<String, SymbolTable> globalST = symbolTable.get(GLOBAL_SCOPE);

		SymbolTable idCurrentScope = scopedST.get(assignedId.image);
		SymbolTable idGlobalScope = globalST.get(assignedId.image);
		if(idGlobalScope == null && idCurrentScope == null) {
			System.out.println("Error: Variable " + assignedId.image + " in " + scopes.peek() + " not declared before assignment.");
		}

		List<Token> tokens = null;

		Object remainder = node.jjtGetChild(1).jjtAccept(this, data);
    System.out.println(remainder);
		if(remainder instanceof Token) {
			tokens = new ArrayList<Token>();
			tokens.add((Token) remainder);
		} else {
			tokens = (List<Token>) remainder;
		}

		if(idCurrentScope != null) {
			if(idCurrentScope.getDataType() == DataType.Constant) {
				System.out.println("Error: Constant " + assignedId.image + " already has a value.");
			} else {
				idCurrentScope.add("written", true);
				idCurrentScope.add("value", tokens);
				//checkTypeOfVariable(idCurrentScope);
			}
		} else if(idGlobalScope != null) {
			if(idGlobalScope.getDataType() == DataType.Constant) {
				System.out.println("Error: Constant " + assignedId.image + " already has a value.");
			} else {
				idGlobalScope.add("writen", true);
				idGlobalScope.add("value", tokens);
				//checkTypeOfVariable(idGlobalScope);
			}
		}
		return null;
  }

  public Object visit(ASTFunctionCall node, Object data) {
    Token id = (Token) node.jjtGetChild(0).jjtAccept(this, data);
		List<Token> args = (List<Token>) node.jjtGetChild(1).jjtAccept(this, data);

		HashMap<String, SymbolTable> globalST = symbolTable.get(GLOBAL_SCOPE);
		SymbolTable funcST = globalST.get(id.image);

    if(funcST != null) {//TODO: Fix IF
  		if(funcST.getDataType() == DataType.Function) {
  			calledFunctions.add(id.image);

  			if(funcST.get("parameters") != null) {
  				List<Token> params = (List<Token>) funcST.get("parameters");
  				if(args.size() != params.size()/2) {
  					System.out.println("Error: Incorrect args size for " + id.image + " in " + scopes.peek());
  				}
  			}
  		} else {
        System.out.println("Error: " + id.image + " is not a function");
      }
    } else {
      System.out.println("Error: The function " + id.image + " doesn't exist");
    }
		return null;
  }

  /*public Object visit(ASTMath_Op node, Object data) {
    return null;
  }*/

  public Object visit(ASTAddOperation node, Object data) {
		//checkExpression(node.jjtGetChild(0).jjtAccept(this, data));
		//checkExpression(node.jjtGetChild(1).jjtAccept(this, data));
		return Arrays.asList(node.jjtGetChild(0).jjtAccept(this, data), node.jjtGetChild(1).jjtAccept(this, data));
  }

  public Object visit(ASTSubOperation node, Object data) {
		//checkExpression(node.jjtGetChild(0).jjtAccept(this, data));
		//checkExpression(node.jjtGetChild(1).jjtAccept(this, data));
		return Arrays.asList(node.jjtGetChild(0).jjtAccept(this, data), node.jjtGetChild(1).jjtAccept(this, data));
  }

  /*public void checkExpression(Object o) {

  }*/

  public Object visit(ASTId node, Object data) {

    Token token = (Token) node.jjtGetValue();
		HashMap<String, SymbolTable> scopedST = symbolTable.get(scopes.peek());
		HashMap<String, SymbolTable> globalST = symbolTable.get(GLOBAL_SCOPE);

    //System.out.println("ScopedST of \"" + scopes.peek() + "\" : " + scopedST);
		SymbolTable idInCurrentScope = scopedST.get(token.image);
		SymbolTable idInGlobalScope = globalST.get(token.image);

		if(scopedST != null && globalST != null) {
			if(idInCurrentScope == null && idInGlobalScope == null) {
				System.out.println("Error: " + token.image + " not declared in this scope before use.");
			}
		}
    /*
		if(idInGlobalScope != null) {
			idInGlobalScope.add("read", true);
			globalST.put(token.image, idInGlobalScope);
		}
		if(idInCurrentScope != null) {
			idInCurrentScope.add("read", true);
			scopedST.put(token.image, idInCurrentScope);
		}*/

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
		for (int i = 0; i < node.jjtGetNumChildren(); i++) {
			args.add((Token) node.jjtGetChild(i).jjtAccept(this, data));
		}
		return args;
	}
}
/*
public class SCVisitor implements CCALVisitor {

	private static HashMap<String, HashMap<String, SymbolTable>> symbolTable = new HashMap<>();

	private static HashSet<String> declaredFunctions = new HashSet<String>();
	private static HashSet<String> calledFunctions = new HashSet<String>();

	private static final String GLOBAL_SCOPE = "Global-Scope";
	private static String currentScope = GLOBAL_SCOPE;
	private static String previousScope = currentScope;

	public HashMap<String, HashMap<String, SymbolTable>> getSymbolTable() {
		return symbolTable;
	}

	public Object visit(ASTProgram node, Object info) { done; }

	public Object visit(ASTConstantDeclaration node, Object info) { done; }

	public Object visit(ASTVariableDeclaration node, Object info) { done; }

	public Object visit(ASTIdentifierList node, Object info) {
		List<Token> tokens = new ArrayList<>();
		for (int i = 0; i < node.jjtGetNumChildren(); i++) {
			SimpleNode convertedNode = (SimpleNode) node.jjtGetChild(i);
			Token identifer = (Token) convertedNode.jjtGetValue();
			tokens.add(identifer);
		}
		return tokens;
	}

	public Object visit(ASTIdentifier node, Object info) { done; }

	public Object visit(ASTBoolean node, Object info) {
		done;
	}

	public Object visit(ASTNumber node, Object info) {
    done;
  }

	public Object visit(ASTAssignment node, Object info) {
		Token assignedIdentifier = (Token) node.jjtGetChild(0).jjtAccept(this, info);

		HashMap<String, SymbolTable> symbolTableScoped = symbolTable.get(currentScope);
		HashMap<String, SymbolTable> scopedST = symbolTable.get(GLOBAL_SCOPE);

		SymbolTable idCurrentScope = symbolTableScoped.get(assignedIdentifier.image);
		SymbolTable idGlobalScope = globalScopeSymbolTable.get(assignedIdentifier.image);
		if(idGlobalScope == null && idCurrentScope == null) {
			System.out.println("Error: Variable " + assignedIdentifier.image + " in " + currentScope + " not declared before assignment.");
		}

		List<Token> tokens = null;

		Object restOfStatement = node.jjtGetChild(1).jjtAccept(this, info);
		if(restOfStatement instanceof Token) {
			tokens = new ArrayList<Token>();
			tokens.add((Token) restOfStatement);
		}

		else {
			tokens = (List<Token>) restOfStatement;
		}

		if(idCurrentScope != null) {
			if(idCurrentScope.getDataType() == Type.CONST) {
				System.out.println("Error: Attempting to assign a value to constant variable " + assignedIdentifier.image);
			}
			else {
				idCurrentScope.add("written", true);
				idCurrentScope.add("value", tokens);
				checkTypeOfVariable(idCurrentScope);
			}
		}

		else if(idGlobalScope != null) {
			if(idGlobalScope.getDataType() == Type.CONST) {
				System.out.println("Error: Attempting to assign a value to constant variable " + assignedIdentifier.image);
			}
			else {
				idGlobalScope.add("writen", true);
				idGlobalScope.add("value", tokens);
				checkTypeOfVariable(idGlobalScope);
			}
		}
		return null;
	}

	private void checkTypeOfVariable(SymbolTable variable) {
		List<Token> tokens = (List<Token>) variable.get("value");
		Token typeOfAssignedVar = variable.getType();

		HashMap<String, SymbolTable> currentScopeSymbolTable = symbolTable.get(currentScope);
		HashMap<String, SymbolTable> globalScopeSymbolTable = symbolTable.get(GLOBAL_SCOPE);

		for (Token token : tokens) {
			SymbolTable varCS = currentScopeSymbolTable.get(token.image);
			SymbolTable varGS = globalScopeSymbolTable.get(token.image);

			if(varCS != null) {
				if(!typeOfAssignedVar.image.equals(varCS.getType().image)) {
					System.out.println("Error: Variable " + variable.getIdentifier().image + " of type " + typeOfAssignedVar.image + " doesn't match " + varCS.getIdentifier().image + " of type " + varCS.getType().image);
					return;
				}
			}

			else if(varGS != null) {
				if(!typeOfAssignedVar.image.equals(varGS.getType().image)) {
					System.out.println("Error: Variable " + variable.getIdentifier().image + " of type " + typeOfAssignedVar.image + " doesn't match " + varGS.getIdentifier().image + " of type " + varGS.getType().image);
					return;
				}
			}
			else {
				if(token.kind == BasicLParserConstants.TRUE || token.kind == BasicLParserConstants.FALSE) {
					if(typeOfAssignedVar.kind != BasicLParserConstants.BOOLEAN) {
						System.out.println("Error: Variable " + variable.getIdentifier().image + " of type " + typeOfAssignedVar.image + " doesn't match boolean constant.");
						return;
					}
				}
				else if(token.kind == BasicLParserConstants.NUMBER) {
					if(typeOfAssignedVar.kind != BasicLParserConstants.INTEGER) {
						System.out.println("Error: Variable " + variable.getIdentifier().image + " of type " + typeOfAssignedVar.image + " doesn't match number constant.");
						return;
					}
				}
			}
		}
	}

	public Object visit(ASTIfCondition node, Object info) {
		node.childrenAccept(this, info);
		return null;
	}

	public Object visit(ASTDivExpression node, Object info) {
		checkExpression(node.jjtGetChild(0).jjtAccept(this, info));
		checkExpression(node.jjtGetChild(1).jjtAccept(this, info));
		return Arrays.asList(node.jjtGetChild(0).jjtAccept(this, info), node.jjtGetChild(1).jjtAccept(this, info));
	}

	public Object visit(ASTMultExpression node, Object info) {
		checkExpression(node.jjtGetChild(0).jjtAccept(this, info));
		checkExpression(node.jjtGetChild(1).jjtAccept(this, info));
		return Arrays.asList(node.jjtGetChild(0).jjtAccept(this, info), node.jjtGetChild(1).jjtAccept(this, info));
	}

	public Object visit(ASTSubExpression node, Object info) {
		checkExpression(node.jjtGetChild(0).jjtAccept(this, info));
		checkExpression(node.jjtGetChild(1).jjtAccept(this, info));
		return Arrays.asList(node.jjtGetChild(0).jjtAccept(this, info), node.jjtGetChild(1).jjtAccept(this, info));
	}

	public Object visit(ASTAddExpression node, Object info) {
		checkExpression(node.jjtGetChild(0).jjtAccept(this, info));
		checkExpression(node.jjtGetChild(1).jjtAccept(this, info));
		return Arrays.asList(node.jjtGetChild(0).jjtAccept(this, info), node.jjtGetChild(1).jjtAccept(this, info));
	}

	private void checkExpression(Object obj) {
		if(!(obj instanceof Token)) return;
		Token identifer = (Token) obj;

		HashMap<String, SymbolTable> globalScopeSymbolTable = symbolTable.get(GLOBAL_SCOPE);
		HashMap<String, SymbolTable> currentScopeSymbolTable = symbolTable.get(currentScope);

		SymbolTable variable = null;

		if(globalScopeSymbolTable.get(identifer.image) != null) {
			variable = globalScopeSymbolTable.get(identifer.image);
		}

		else if(currentScopeSymbolTable.get(identifer.image) != null) {
			variable = currentScopeSymbolTable.get(identifer.image);
		}

		if(variable != null) {
			if(variable.getDataType() == Type.VAR) {
				System.out.println(variable.getIdentifier().image + " is a variable.");
			}

			else if(variable.getDataType() == Type.CONST) {
				System.out.println(variable.getIdentifier().image + " is a constant.");
			}
		}
	}

	public Object visit(ASTCondition node, Object info) {
		node.childrenAccept(this, info);
		return null;
	}

	public Object visit(ASTSubFrag node, Object info) {
		node.childrenAccept(this, info);
		return null;
	}

	public Object visit(ASTAddFrag node, Object info) {
		node.childrenAccept(this, info);
		return null;
	}

	public Object visit(ASTType node, Object info) {
		return node.jjtGetValue();
	}

	public Object visit(ASTParameterList node, Object info) {
		List<Token> tokens = new ArrayList<Token>();
		for (int i = 0; i < node.jjtGetNumChildren(); i += 1) {
			SimpleNode convertedNode = (SimpleNode) node.jjtGetChild(i);
			Token child = (Token) convertedNode.jjtGetValue();
			tokens.add(child);
		}
		return tokens;
	}

	public Object visit(ASTArgumentList node, Object info) {
		List<Token> arguments = new ArrayList<Token>();
		for (int i = 0; i < node.jjtGetNumChildren(); i++) {
			arguments.add((Token) node.jjtGetChild(i).jjtAccept(this, info));
		}
		return arguments;
	}

	public Object visit(ASTFunctionCall node, Object info) {
		Token identifer = (Token) node.jjtGetChild(0).jjtAccept(this, info);
		List<Token> arguments = (List<Token>) node.jjtGetChild(1).jjtAccept(this, info);

		HashMap<String, SymbolTable> globalScopeSymbolTable = symbolTable.get(GLOBAL_SCOPE);
		SymbolTable function = globalScopeSymbolTable.get(identifer.image);

		if(function != null && function.getDataType() == Type.FUNCTION) {
			calledFunctions.add(identifer.image);
			if(function.get("parameters") != null) {
				List<Token> parameters = (List<Token>) function.get("parameters");
				if(arguments.size() != parameters.size()/2) {
					System.out.println("Error: Incorrect arguments size for " + identifer.image + " in " + currentScope);
				}
			}
		}

		else System.out.println("Error: No such function as " + identifer.image);
		return null;
	}

	public Object visit(ASTFunction node, Object info) {
		Token type = (Token) node.jjtGetChild(0).jjtAccept(this, info);
		SimpleNode convertedNode = (SimpleNode) node.jjtGetChild(1);
		Token identifer = (Token) convertedNode.jjtGetValue();
		List<Token> parameterList = (List<Token>) node.jjtGetChild(2).jjtAccept(this, info);

		SymbolTable function = new SymbolTable(identifer, type, currentScope, Type.FUNCTION);
		function.add("parameters", parameterList);

		HashMap<String, SymbolTable> symbolTableAtGlobalScope = symbolTable.get(currentScope);
		if(symbolTableAtGlobalScope.get(identifer.image) == null) {
			symbolTableAtGlobalScope.put(identifer.image, function);
			symbolTable.put(currentScope, symbolTableAtGlobalScope);
		}

		else {
			System.out.println(identifer.image + " already declared in " + currentScope);
		}

		//change scope for func block.
		previousScope = currentScope;
		currentScope = "Function: " + identifer.image;

		for (int i = 0; i < parameterList.size(); i += 2) {
			Token paramIdentifier = parameterList.get(i);
			Token paramType = parameterList.get(i + 1);
			SymbolTable funcVar = new SymbolTable(paramIdentifier, paramType, currentScope, Type.VAR);

			HashMap<String, SymbolTable> symbolTableForFunction = symbolTable.get(currentScope);
			if(symbolTableForFunction == null) symbolTableForFunction = new HashMap<>();

			if(symbolTableAtGlobalScope.get(paramIdentifier.image) != null) {
				System.out.println("Error: Parameter " + paramIdentifier.image + " of type " + paramType.image + " already in scope " + GLOBAL_SCOPE);
			}

			else if(symbolTableForFunction.get(paramIdentifier.image) == null) {
				symbolTableForFunction.put(paramIdentifier.image, funcVar);
				symbolTable.put(currentScope, symbolTableForFunction);
			}

			else {
				System.out.println("Parameter " + paramIdentifier.image + " of type " + paramType.image + " already in scope " + currentScope);
			}
		}

		Token functionBody = (Token) node.jjtGetChild(3).jjtAccept(this, info);
		declaredFunctions.add(identifer.image);
		currentScope = previousScope;

		return null;
	}

	public Object visit(ASTFunctionBody node, Object info) {
		node.childrenAccept(this, info);
		return null;
	}

	public Object visit(ASTMain node, Object info) {
		previousScope = currentScope;
		currentScope = "Main-Scope";
		node.childrenAccept(this, info);
		currentScope = previousScope;
		return null;
	}

	public Object visit(SimpleNode node, Object info) {
		return null;
	}
}*/
