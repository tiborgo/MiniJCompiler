package minijava.intermediate.tree;

public class TreeStmSEQ extends TreeStm {

  public final TreeStm first, second;

  public TreeStmSEQ(TreeStm first, TreeStm second) {
    if (first == null || second == null) {
      throw new NullPointerException();
    }
    this.first = first;
    this.second = second;
  }

  @Override
  public <A> A accept(TreeStmVisitor<A> visitor) {
    return visitor.visit(this);
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder();
    buf.append("SEQ(");
    buf.append(first);
    buf.append(", ");
    buf.append(second);
    buf.append(") ");
    return buf.toString();
  }

}
