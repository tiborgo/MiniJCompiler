package minijava.intermediate.tree;

import minijava.intermediate.visitors.TreeStmVisitor;

public class TreeStmEXP extends TreeStm {
  public final TreeExp exp;

  public TreeStmEXP(TreeExp exp) {
    if (exp == null) {
      throw new NullPointerException();
    }
    this.exp = exp;
  }

  @Override
  public <A, T extends Throwable> A accept(TreeStmVisitor<A, T> visitor) throws T {
    return visitor.visit(this);
  }

  @Override
  public String toString() {
    return "EXP(" + exp + ")";
  }
}
