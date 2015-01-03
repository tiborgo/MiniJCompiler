package minijava.ast.rules.expressions;

public class ExpNew extends Exp {

  final public String className;

  public ExpNew(String className) {
    this.className = className;
  }

  @Override
  public <A, T extends Throwable> A accept(ExpThis.ExpVisitor<A, T> v) throws T {
    return v.visit(this);
  }
}
