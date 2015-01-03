package minijava.ast.rules.expressions;

public class NewIntArray extends Expression {

  final public Expression size;

  public NewIntArray(Expression size) {
    this.size = size;
  }

  @Override
  public <A, T extends Throwable> A accept(ExpressionVisitor<A, T> v) throws T {
    return v.visit(this);
  }
}
