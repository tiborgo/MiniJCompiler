package minijava.parse.rules.expressions;

public class False extends Expression {

  public False() {
  }

  @Override
  public <A, T extends Throwable> A accept(ExpressionVisitor<A, T> v) throws T {
    return v.visit(this);
  }
}
