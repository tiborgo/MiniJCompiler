package minijava.parse.rules.expressions;

public class ArrayGet extends Expression {

  final public Expression array;
  final public Expression index;

  public ArrayGet(Expression array, Expression index) {
    this.array = array;
    this.index = index;
  }

  @Override
  public <A, T extends Throwable> A accept(ExpressionVisitor<A, T> v) throws T {
    return v.visit(this);
  }
}
