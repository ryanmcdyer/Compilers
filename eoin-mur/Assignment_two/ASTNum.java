/* Generated By:JJTree: Do not edit this line. ASTNum.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTNum extends SimpleNode {
  public ASTNum(int id) {
    super(id);
  }

  public ASTNum(BasicL p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(BasicLVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=f34d453b0c734c6a0326dd7f33937bbb (do not edit this line) */
