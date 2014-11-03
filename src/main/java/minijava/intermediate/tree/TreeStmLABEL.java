package minijava.intermediate.tree;

import minijava.intermediate.Label;

public class TreeStmLABEL extends TreeStm {

  public final Label label;

  public TreeStmLABEL(Label label) {
    if (label == null) {
      throw new NullPointerException();
    }
    this.label = label;
  }

  @Override
  public <A> A accept(TreeStmVisitor<A> visitor) {
    return visitor.visit(this);
  }

  @Override
  public String toString() {
    return "LABEL(" + label + ")";
  }
}
