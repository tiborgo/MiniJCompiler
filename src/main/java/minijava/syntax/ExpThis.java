package minijava.syntax;

public class ExpThis extends Exp {

  public ExpThis() {
  }

  @Override
  public <A, T extends Throwable> A accept(ExpVisitor<A, T> v) throws T {
    return v.visit(this);
  }
}
