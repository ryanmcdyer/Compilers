/* Generated By:JJTree: Do not edit this line. ASTVar_decl.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTVar_decl extends SimpleNode {
  public ASTVar_decl(int id) {
    super(id);
  }

  public ASTVar_decl(CCAL p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(CCALVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=f13c25f38b8bcb2504456cf51b281c9f (do not edit this line) */
