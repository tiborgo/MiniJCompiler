package minijava.syntax;

public class ExpBinOp extends Exp {

  public enum Op {

    // note: there is no OR in MiniJava (and actually also no DIV)
    PLUS("+"), MINUS("-"), TIMES("*"), DIV("/"), AND("&&"), LT("<");

    private final String name;

    Op(String name) {
      this.name = name;
    }

    @Override
    public String toString() {
      return name;
    }
  };
  final public Exp left;
  final public Op op;
  final public Exp right;

  public ExpBinOp(Exp e1, Op op, Exp e2) {
    this.left = e1;
    this.op = op;
    this.right = e2;
  }

  @Override
  public <A, T extends Throwable> A accept(ExpVisitor<A, T> v) throws T{
    return v.visit(this);
  }
}
