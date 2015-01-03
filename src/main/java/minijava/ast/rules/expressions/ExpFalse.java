package minijava.ast.rules.expressions;

import minijava.ast.visitors.ExpVisitor;

public class ExpFalse extends Exp {

  public ExpFalse() {
  }

  @Override
  public <A, T extends Throwable> A accept(ExpVisitor<A, T> v) throws T {
    return v.visit(this);
  }
}
