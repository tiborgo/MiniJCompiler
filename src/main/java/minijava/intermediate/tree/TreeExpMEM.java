package minijava.intermediate.tree;

public class TreeExpMEM extends TreeExp {
  public final TreeExp addr;

  public TreeExpMEM(TreeExp addr) {
    if (addr == null) {
      throw new NullPointerException();
    }
    this.addr = addr;
  }

  @Override
  public <A> A accept(TreeExpVisitor<A> visitor) {
    return visitor.visit(this);
  }

  @Override
  public String toString() {
    return "MEM(" + addr + ")";
  }

}
