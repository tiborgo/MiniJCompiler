package minijava.parse.rules.expressions;

public class Negate extends Expression {

  final public Expression body;

  public Negate(Expression body) {
    this.body = body;
  }

  @Override
  public <A, T extends Throwable> A accept(ExpressionVisitor<A, T> v) throws T {
    return v.visit(this);
  }
}
