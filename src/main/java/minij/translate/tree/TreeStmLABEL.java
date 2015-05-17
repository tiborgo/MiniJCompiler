package minij.translate.tree;

import minij.translate.layout.Label;

public class TreeStmLABEL extends TreeStm {

  public final Label label;

  public TreeStmLABEL(Label label) {
    if (label == null) {
      throw new NullPointerException();
    }
    this.label = label;
  }

  @Override
  public <A, T extends Throwable> A accept(TreeStmVisitor<A, T> visitor) throws T {
    return visitor.visit(this);
  }

  @Override
  public String toString() {
    return "LABEL(" + label + ")";
  }
}
