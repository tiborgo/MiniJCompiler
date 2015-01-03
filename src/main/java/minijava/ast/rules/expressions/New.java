package minijava.ast.rules.expressions;

public class New extends Expression {

  final public String className;

  public New(String className) {
    this.className = className;
  }

  @Override
  public <A, T extends Throwable> A accept(ExpressionVisitor<A, T> v) throws T {
    return v.visit(this);
  }
}
