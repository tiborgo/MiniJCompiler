package minijava.parse.rules.expressions;

public class BinOp extends Expression {

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
  final public Expression left;
  final public Op op;
  final public Expression right;

  public BinOp(Expression e1, Op op, Expression e2) {
    this.left = e1;
    this.op = op;
    this.right = e2;
  }

  @Override
  public <A, T extends Throwable> A accept(ExpressionVisitor<A, T> v) throws T{
    return v.visit(this);
  }
}
