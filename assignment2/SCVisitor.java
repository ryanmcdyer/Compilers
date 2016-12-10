import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
/*
public class SCVisitor implements CCALVisitor {

  private HashMap<String, HashMap<String, SymbolTable>> symbolTable = new HashMap<>();

  private static HashSet<String> declaredFunctions = new HashSet<String>();
  private static HashSet<String> calledFunctions = new HashSet<String>();

  private static final String GLOBAL_SCOPE = "Global";
  private static String currentScope = GLOBAL_SCOPE;
  private static String previousScope = currentScope;

	public HashMap<String, HashMap<String, SymbolTable>> getSymbolTable() {
		return symbolTable;
	}

  public Object visit(SimpleNode node, Object data) {
    //return null;
  }
  public Object visit(ASTProgram node, Object data) {
    //ScopedTable goes here
  		symbolTable.put(currentScope, new HashMap<String, SymbolTable>());
  		node.childrenAccept(this, data);

      System.out.println(declaredFunctions.size());
      System.out.println(calledFunctions.size());

  		if (declaredFunctions.size() == calledFunctions.size()) {
  			System.out.println("All " + declaredFunctions.size() + " functions were called.");
  		} else {
        Set<String> diff = declaredFunctions;
        diff.removeAll(calledFunctions);
  			System.out.println(diff.size() + " of " + declaredFunctions.size() + " declared functions were not called. They are: ");
        for(String s : diff) {
          System.out.println("\t" + s);
        }
  		}

  		for (String scope : symbolTable.keySet()) {
  			HashMap<String, SymbolTable> scopedSymbolTable = symbolTable.get(scope);
  			System.out.println("Scope: " + scope);

  			for (String id : scopedSymbolTable.keySet()) {
  				SymbolTable st = scopedSymbolTable.get(id);
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
    for (int i = 0; i < node.jjtGetNumChildren(); i += 2) {
      List<Token> identifierList = (List<Token>) node.jjtGetChild(i).jjtAccept(this, data);
      Token type = (Token) node.jjtGetChild(i+1).jjtAccept(this, data);

      for (Token identifer : identifierList) {
        HashMap<String, SymbolTable> symbolTableScoped = symbolTable.get(currentScope);
        if(symbolTableScoped == null) {
          symbolTableScoped = new HashMap<>();
        }
        SymbolTable var = new SymbolTable(identifer, type, currentScope, DataType.VariableDecl);
        HashMap<String, SymbolTable> globalScopeSymbolTable = symbolTable.get(GLOBAL_SCOPE);

        if(globalScopeSymbolTable.get(identifer.image) != null) {
          System.out.println("Error: Variable " + identifer.image + " already declared in " + GLOBAL_SCOPE);
        } else if(symbolTableScoped.get(identifer.image) == null) {
          symbolTableScoped.put(identifer.image, var);
          symbolTable.put(currentScope, symbolTableScoped);
        } else {
          System.out.println("Error: " + identifer.image + " was already declared in " + currentScope);
        }
    }
  }
  return null;
  }

  public Object visit(ASTConst_decl node, Object data) {
    HashMap<String, SymbolTable> scopedST = symbolTable.get(currentScope);
    if(scopedST == null) {
      scopedST = new HashMap<>();
    }

    for (int i = 0; i < node.jjtGetNumChildren(); i += 3) {
      SimpleNode convertedNode = (SimpleNode) node.jjtGetChild(i);
      Token identifer = (Token) convertedNode.jjtGetValue();
      Token type = (Token) node.jjtGetChild(i + 1).jjtAccept(this, data);
      List<Token> assign;

      if (node.jjtGetChild(i+2).jjtAccept(this, data) instanceof Token) {
        assign = new ArrayList<>();
        assign.add((Token) node.jjtGetChild(i+2).jjtAccept(this, data));
      }
      else {
        assign = Arrays.asList((Token) node.jjtGetChild(i+2).jjtAccept(this, data));
      }

      SymbolTable tmpST = new SymbolTable(identifer, type, currentScope, DataType.ConstantDecl);
      tmpST.add("value", assign);

      HashMap<String, SymbolTable> globalScopeSymbolTable = symbolTable.get(GLOBAL_SCOPE);
      //if (globalScopeSymbolTable.get(identifer.image) != null) {
      if (globalScopeSymbolTable.containsKey(identifer.image)) {
        System.out.println("Error: Constant " + identifer.image + " already declared in " + GLOBAL_SCOPE);
      } else if(scopedST.get(identifer.image) == null) {
        scopedST.put(identifer.image, tmpST);
        symbolTable.put(currentScope, scopedST);
      }

      else {
        System.out.println("Error: " + identifer.image + " was already declared in " + currentScope);
      }
    }
    return null;
  }

  public Object visit(ASTFunction node, Object data) {
    return null;
  }

  public Object visit(ASTType node, Object data) {
    return null;
  }

  public Object visit(ASTParam node, Object data) {
    return null;
  }

  public Object visit(ASTMain node, Object data) {
    return null;
  }

  public Object visit(ASTStatement node, Object data) {
    return null;
  }

  public Object visit(ASTIfCond node, Object data) {
    return null;
  }

  public Object visit(ASTWhileCond node, Object data) {
    return null;
  }

  public Object visit(ASTAssignment node, Object data) {
    return null;

  }
  public Object visit(ASTMath_Op node, Object data) {
    return null;
  }

  public Object visit(ASTAddOperation node, Object data);
  public Object visit(ASTSubOperation node, Object data);

  public Object visit(ASTFragment node, Object data) {
    return null;
  }

  public Object visit(ASTId node, Object data) {
    return null;
  }

  public Object visit(ASTNumber node, Object data) {
    return null;
  }

  public Object visit(ASTBoolean node, Object data) {
    return null;
  }

  public Object visit(ASTLogic_Op node, Object data) {
    return null;
  }

  public Object visit(ASTCompare_Op node, Object data) {
    return null;
  }

  public Object visit(ASTArg node, Object data) {
    return null;
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

	public Object visit(ASTProgram node, Object info) {
    done;
	}

	public Object visit(ASTConstantDeclaration node, Object info) {
    done;
	}

	public Object visit(ASTVariableDeclaration node, Object info) {
		for (int i = 0; i < node.jjtGetNumChildren(); i += 2) {
			List<Token> identifierList = (List<Token>) node.jjtGetChild(i).jjtAccept(this, info);
			Token type = (Token) node.jjtGetChild(i+1).jjtAccept(this, info);

			for (Token identifer : identifierList) {
				HashMap<String, SymbolTable> symbolTableScoped = symbolTable.get(currentScope);
				if (symbolTableScoped == null) symbolTableScoped = new HashMap<>();
				SymbolTable var = new SymbolTable(identifer, type, currentScope, Type.VAR);
				HashMap<String, SymbolTable> globalScopeSymbolTable = symbolTable.get(GLOBAL_SCOPE);

            	if (globalScopeSymbolTable.get(identifer.image) != null) {
            		System.out.println("Error: Variable " + identifer.image + " already declared in " + GLOBAL_SCOPE);
            	}

				else if (symbolTableScoped.get(identifer.image) == null) {
					symbolTableScoped.put(identifer.image, var);
					symbolTable.put(currentScope, symbolTableScoped);
				}

				else System.out.println("Error: " + identifer.image + " was already declared in " + currentScope);
			}
		}
		return null;
	}

	public Object visit(ASTIdentifierList node, Object info) {
		List<Token> tokens = new ArrayList<>();
		for (int i = 0; i < node.jjtGetNumChildren(); i++) {
			SimpleNode convertedNode = (SimpleNode) node.jjtGetChild(i);
			Token identifer = (Token) convertedNode.jjtGetValue();
			tokens.add(identifer);
		}
		return tokens;
	}

	public Object visit(ASTIdentifier node, Object info) {
		Token token = (Token) node.jjtGetValue();
		HashMap<String, SymbolTable> symbolTableScoped = symbolTable.get(currentScope);
		HashMap<String, SymbolTable> globalScopeSymbolTable = symbolTable.get(GLOBAL_SCOPE);

		SymbolTable idCurrentScope = symbolTableScoped.get(token.image);
		SymbolTable idGlobalScope = globalScopeSymbolTable.get(token.image);

		if (symbolTableScoped != null && globalScopeSymbolTable != null) {
			if (idCurrentScope == null && idGlobalScope == null) {
				System.out.println("Error: " + token.image + " was not declared before use.");
			}
		}

		if (idGlobalScope != null) {
			idGlobalScope.add("read", true);
			globalScopeSymbolTable.put(token.image, idGlobalScope);
		}

		if (idCurrentScope != null) {
			idCurrentScope.add("read", true);
			symbolTableScoped.put(token.image, idCurrentScope);
		}

		return node.jjtGetValue();
	}

	public Object visit(ASTBoolean node, Object info) {
		Token token = (Token) node.jjtGetValue();
		//token.kind = BasicLParserConstants.BOOLEAN;
		return token;
	}

	public Object visit(ASTNumber node, Object info) {
		return node.jjtGetValue();
	}

	public Object visit(ASTAssignment node, Object info) {
		Token assignedIdentifier = (Token) node.jjtGetChild(0).jjtAccept(this, info);

		HashMap<String, SymbolTable> symbolTableScoped = symbolTable.get(currentScope);
		HashMap<String, SymbolTable> globalScopeSymbolTable = symbolTable.get(GLOBAL_SCOPE);

		SymbolTable idCurrentScope = symbolTableScoped.get(assignedIdentifier.image);
		SymbolTable idGlobalScope = globalScopeSymbolTable.get(assignedIdentifier.image);
		if (idGlobalScope == null && idCurrentScope == null) {
			System.out.println("Error: Variable " + assignedIdentifier.image + " in " + currentScope + " not declared before assignment.");
		}

		List<Token> tokens = null;

		Object restOfStatement = node.jjtGetChild(1).jjtAccept(this, info);
		if (restOfStatement instanceof Token) {
			tokens = new ArrayList<Token>();
			tokens.add((Token) restOfStatement);
		}

		else {
			tokens = (List<Token>) restOfStatement;
		}

		if (idCurrentScope != null) {
			if (idCurrentScope.getDataType() == Type.CONST) {
				System.out.println("Error: Attempting to assign a value to constant variable " + assignedIdentifier.image);
			}
			else {
				idCurrentScope.add("written", true);
				idCurrentScope.add("value", tokens);
				checkTypeOfVariable(idCurrentScope);
			}
		}

		else if (idGlobalScope != null) {
			if (idGlobalScope.getDataType() == Type.CONST) {
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

			if (varCS != null) {
				if (!typeOfAssignedVar.image.equals(varCS.getType().image)) {
					System.out.println("Error: Variable " + variable.getIdentifier().image + " of type " + typeOfAssignedVar.image + " doesn't match " + varCS.getIdentifier().image + " of type " + varCS.getType().image);
					return;
				}
			}

			else if (varGS != null) {
				if (!typeOfAssignedVar.image.equals(varGS.getType().image)) {
					System.out.println("Error: Variable " + variable.getIdentifier().image + " of type " + typeOfAssignedVar.image + " doesn't match " + varGS.getIdentifier().image + " of type " + varGS.getType().image);
					return;
				}
			}
			else {
				if (token.kind == BasicLParserConstants.TRUE || token.kind == BasicLParserConstants.FALSE) {
					if (typeOfAssignedVar.kind != BasicLParserConstants.BOOLEAN) {
						System.out.println("Error: Variable " + variable.getIdentifier().image + " of type " + typeOfAssignedVar.image + " doesn't match boolean constant.");
						return;
					}
				}
				else if (token.kind == BasicLParserConstants.NUMBER) {
					if (typeOfAssignedVar.kind != BasicLParserConstants.INTEGER) {
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
		if (!(obj instanceof Token)) return;
		Token identifer = (Token) obj;

		HashMap<String, SymbolTable> globalScopeSymbolTable = symbolTable.get(GLOBAL_SCOPE);
		HashMap<String, SymbolTable> currentScopeSymbolTable = symbolTable.get(currentScope);

		SymbolTable variable = null;

		if (globalScopeSymbolTable.get(identifer.image) != null) {
			variable = globalScopeSymbolTable.get(identifer.image);
		}

		else if (currentScopeSymbolTable.get(identifer.image) != null) {
			variable = currentScopeSymbolTable.get(identifer.image);
		}

		if (variable != null) {
			if (variable.getDataType() == Type.VAR) {
				System.out.println(variable.getIdentifier().image + " is a variable.");
			}

			else if (variable.getDataType() == Type.CONST) {
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

		if (function != null && function.getDataType() == Type.FUNCTION) {
			calledFunctions.add(identifer.image);
			if (function.get("parameters") != null) {
				List<Token> parameters = (List<Token>) function.get("parameters");
				if (arguments.size() != parameters.size()/2) {
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
		if (symbolTableAtGlobalScope.get(identifer.image) == null) {
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
			if (symbolTableForFunction == null) symbolTableForFunction = new HashMap<>();

			if (symbolTableAtGlobalScope.get(paramIdentifier.image) != null) {
				System.out.println("Error: Parameter " + paramIdentifier.image + " of type " + paramType.image + " already in scope " + GLOBAL_SCOPE);
			}

			else if (symbolTableForFunction.get(paramIdentifier.image) == null) {
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
