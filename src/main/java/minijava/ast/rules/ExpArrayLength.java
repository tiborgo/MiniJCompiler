package minijava.ast.rules;

import minijava.ast.visitors.ExpVisitor;


public class ExpArrayLength extends Exp {

  final public Exp array;

  public ExpArrayLength(Exp body) {
    this.array = body;
  }

  @Override
  public <A, T extends Throwable> A accept(ExpVisitor<A, T> v) throws T{
    return v.visit(this);
  }
}
