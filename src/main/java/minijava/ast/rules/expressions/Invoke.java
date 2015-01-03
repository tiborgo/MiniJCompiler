package minijava.ast.rules.expressions;

import java.util.List;

public class Invoke extends Expression {

  final public Expression obj;
  final public String method;
  final public List<Expression> args;

  public Invoke(Expression obj, String method, List<Expression> args) {
    this.obj = obj;
    this.method = method;
    this.args = args;
  }

  @Override
  public <A, T extends Throwable> A accept(ExpressionVisitor<A, T> v) throws T{
    return v.visit(this);
  }
}
