/* Generated By:JJTree: Do not edit this line. ASTLogic_Op.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTLogic_Op extends SimpleNode {
  public ASTLogic_Op(int id) {
    super(id);
  }

  public ASTLogic_Op(CCAL p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(CCALVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=2a302138e5c019db38457d8d7bfc9fe7 (do not edit this line) */
