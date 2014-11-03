package minijava.ast.rules;

import minijava.ast.visitors.ExpVisitor;

public class ExpNewIntArray extends Exp {

  final public Exp size;

  public ExpNewIntArray(Exp size) {
    this.size = size;
  }

  @Override
  public <A, T extends Throwable> A accept(ExpVisitor<A, T> v) throws T {
    return v.visit(this);
  }
}
