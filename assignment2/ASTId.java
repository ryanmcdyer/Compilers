/* Generated By:JJTree: Do not edit this line. ASTId.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTId extends SimpleNode {
  public ASTId(int id) {
    super(id);
  }

  public ASTId(CCAL p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(CCALVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=39b5b97a672dd5629d3dcc25127fa3d7 (do not edit this line) */
