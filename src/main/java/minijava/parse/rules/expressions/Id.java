package minijava.parse.rules.expressions;


public class Id extends Expression {

  final public String id;

  public Id(String id) {
    this.id = id;
  }

  @Override
  public <A, T extends Throwable> A accept(ExpressionVisitor<A, T> v) throws T{
    return v.visit(this);
  }
}
