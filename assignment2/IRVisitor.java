import java.util.*;

public class IRVisitor implements CCALVisitor {

  /*
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

    public Object visit(SimpleNode node, Object data) {
      node.childrenAccept(this, data);
      return null;
    }
    public Object visit(ASTProgram node, Object data) {
      return null;
    }
    public Object visit(ASTVar_decl node, Object data) {
      return null;
    }
    public Object visit(ASTConst_decl node, Object data) {
      return null;
    }
    public Object visit(ASTFunctionList node, Object data) {
       return null;
    }
    public Object visit(ASTFunc_decl node, Object data) {
       return null;
    }
    public Object visit(ASTFunc_body node, Object data) {
       return null;
    }
    public Object visit(ASTType node, Object data) {
       return null;
    }
    public Object visit(ASTParamList node, Object data) {
       return null;
    }
    public Object visit(ASTMain node, Object data) {
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
    public Object visit(ASTFunctionCall node, Object data) {
       return null;
    }
    public Object visit(ASTAddOperation node, Object data) {
      return null;
    }
    public Object visit(ASTSubOperation node, Object data) {
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
    public Object visit(ASTArgList node, Object data) {
       return null;
    }
  }
