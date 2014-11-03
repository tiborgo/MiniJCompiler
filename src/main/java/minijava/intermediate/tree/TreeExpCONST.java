package minijava.intermediate.tree;

public class TreeExpCONST extends TreeExp {
  public final int value;

  public TreeExpCONST(int value) {
    this.value = value;
  }

  @Override
  public <A> A accept(TreeExpVisitor<A> visitor) {
    return visitor.visit(this);
  }

  @Override
  public String toString() {
    return "CONST(" + value + ")";
  }
}
