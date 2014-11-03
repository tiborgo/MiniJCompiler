package minijava.intermediate.tree;

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
  public <A> A accept(TreeStmVisitor<A> visitor) {
    return visitor.visit(this);
  }

  @Override
  public String toString() {
    return "MOVE(" + dest + ", " + src + ")";
  }
}
