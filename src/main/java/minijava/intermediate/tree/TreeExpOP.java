package minijava.intermediate.tree;

import minijava.intermediate.visitors.TreeExpVisitor;

public class TreeExpOP extends TreeExp{

    public enum Op {
      PLUS("+"), MINUS("-"), MUL("*"), DIV("/"), AND("&"), OR("|"),
      LSHIFT("<<"), RSHIFT(">>"), ARSHIFT(">|"), XOR("^");
      
      private final String pretty;
      private Op(String pretty) {
        this.pretty = pretty;
      }

    @Override
      public String toString() {
        return pretty;
      }
    }

    public final Op op;
    public final TreeExp left, right;

  public TreeExpOP(Op op, TreeExp left, TreeExp right) {
    if (op == null || left == null || right == null) {
      throw new NullPointerException();
    }
    this.op = op;
    this.left = left;
    this.right = right;
  }

  @Override
  public <A, T extends Throwable> A accept(TreeExpVisitor<A, T> visitor) throws T {
    return visitor.visit(this);
  }

  @Override
  public String toString() {
    return "OP(" + op + ", " + left + ", " + right + ")";
  }

}
