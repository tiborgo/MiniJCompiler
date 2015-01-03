package minijava.ast.rules.expressions;

import minijava.ast.visitors.ExpVisitor;

public class ExpTrue extends Exp {

  public ExpTrue() {
  }

  @Override
  public <A, T extends Throwable> A accept(ExpVisitor<A, T> v) throws T{
    return v.visit(this);
  }
}
