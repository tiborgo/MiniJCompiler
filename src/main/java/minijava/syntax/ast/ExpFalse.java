package minijava.syntax.ast;

public class ExpFalse extends Exp {

  public ExpFalse() {
  }

  @Override
  public <A, T extends Throwable> A accept(ExpVisitor<A, T> v) throws T {
    return v.visit(this);
  }
}