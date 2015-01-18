package minijava.parse.rules.expressions;

public class True extends Expression {

  public True() {
  }

  @Override
  public <A, T extends Throwable> A accept(ExpressionVisitor<A, T> v) throws T{
    return v.visit(this);
  }
}
