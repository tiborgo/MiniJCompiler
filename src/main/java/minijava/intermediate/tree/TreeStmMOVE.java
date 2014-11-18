package minijava.intermediate.tree;

import minijava.intermediate.visitors.TreeStmVisitor;

public class TreeStmMOVE extends TreeStm {

  public final TreeExp dest, src;

  public TreeStmMOVE(TreeExp dest, TreeExp src) {
    if (dest == null || src == null) {
      throw new NullPointerException();
    }
    this.dest = dest;
    this.src = src;
  }

  @Override
  public <A, T extends Throwable> A accept(TreeStmVisitor<A, T> visitor) throws T {
    return visitor.visit(this);
  }

  @Override
  public String toString() {
    return "MOVE(" + dest + ", " + src + ")";
  }
}
