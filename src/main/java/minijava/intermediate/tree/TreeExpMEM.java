package minijava.intermediate.tree;

import minijava.intermediate.visitors.TreeExpVisitor;

public class TreeExpMEM extends TreeExp {
  public final TreeExp addr;

  public TreeExpMEM(TreeExp addr) {
    if (addr == null) {
      throw new NullPointerException();
    }
    this.addr = addr;
  }

  @Override
  public <A, T extends Throwable> A accept(TreeExpVisitor<A, T> visitor) throws T {
    return visitor.visit(this);
  }

  @Override
  public String toString() {
    return "MEM(" + addr + ")";
  }

}
