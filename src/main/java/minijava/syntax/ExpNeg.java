package minijava.syntax;

public class ExpNeg extends Exp {

  final public Exp body;

  public ExpNeg(Exp body) {
    this.body = body;
  }

  @Override
  public <A, T extends Throwable> A accept(ExpVisitor<A, T> v) throws T {
    return v.visit(this);
  }
}
