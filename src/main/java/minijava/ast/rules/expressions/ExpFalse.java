package minijava.ast.rules.expressions;

public class ExpFalse extends Exp {

  public ExpFalse() {
  }

  @Override
  public <A, T extends Throwable> A accept(ExpThis.ExpVisitor<A, T> v) throws T {
    return v.visit(this);
  }
}
