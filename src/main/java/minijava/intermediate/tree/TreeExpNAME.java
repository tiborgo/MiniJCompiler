package minijava.intermediate.tree;

import minijava.intermediate.Label;
import minijava.intermediate.visitors.TreeExpVisitor;

public class TreeExpNAME extends TreeExp {

  public final Label label;

  public TreeExpNAME(Label label) {
    if (label == null) {
      throw new NullPointerException();
    }
    this.label = label;
  }

  @Override
  public <A, T extends Throwable> A accept(TreeExpVisitor<A, T> visitor) throws T {
    return visitor.visit(this);
  }

  @Override
  public String toString() {
    return "NAME(" + label + ")";
  }
}
