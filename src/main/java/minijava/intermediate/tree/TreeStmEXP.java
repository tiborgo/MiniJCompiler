package minijava.intermediate.tree;

public class TreeStmEXP extends TreeStm {
  public final TreeExp exp;

  public TreeStmEXP(TreeExp exp) {
    if (exp == null) {
      throw new NullPointerException();
    }
    this.exp = exp;
  }

  @Override
  public <A> A accept(TreeStmVisitor<A> visitor) {
    return visitor.visit(this);
  }

  @Override
  public String toString() {
    return "EXP(" + exp + ")";
  }
}
