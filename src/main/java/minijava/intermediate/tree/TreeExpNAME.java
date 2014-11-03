package minijava.intermediate.tree;

import minijava.intermediate.Label;

public class TreeExpNAME extends TreeExp {

  public final Label label;

  public TreeExpNAME(Label label) {
    if (label == null) {
      throw new NullPointerException();
    }
    this.label = label;
  }

  @Override
  public <A> A accept(TreeExpVisitor<A> visitor) {
    return visitor.visit(this);
  }

  @Override
  public String toString() {
    return "NAME(" + label + ")";
  }
}
