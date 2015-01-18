package minijava.translate.tree;

import minijava.translate.visitors.TreeExpVisitor;

public class TreeExpCONST extends TreeExp {
  public final int value;

  public TreeExpCONST(int value) {
    this.value = value;
  }

  @Override
  public <A, T extends Throwable> A accept(TreeExpVisitor<A, T> visitor) throws T {
    return visitor.visit(this);
  }

  @Override
  public String toString() {
    return "CONST(" + value + ")";
  }
}
