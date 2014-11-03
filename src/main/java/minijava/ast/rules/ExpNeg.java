package minijava.ast.rules;

import minijava.ast.visitors.ExpVisitor;

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
