package minijava.ast.rules.expressions;

public class This extends Expression {

  public This() {
  }

  @Override
  public <A, T extends Throwable> A accept(ExpressionVisitor<A, T> v) throws T {
    return v.visit(this);
  }

}
