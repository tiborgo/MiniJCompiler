package minijava.parse.rules.expressions;


public class ArrayLength extends Expression {

  final public Expression array;

  public ArrayLength(Expression body) {
    this.array = body;
  }

  @Override
  public <A, T extends Throwable> A accept(ExpressionVisitor<A, T> v) throws T{
    return v.visit(this);
  }
}
