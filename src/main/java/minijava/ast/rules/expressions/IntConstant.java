package minijava.ast.rules.expressions;


public class IntConstant extends Expression {

  final public int value;

  public IntConstant(int value) {
    this.value = value;
  }

  @Override
  public <A, T extends Throwable> A accept(ExpressionVisitor<A, T> v) throws T{
    return v.visit(this);
  }
}
