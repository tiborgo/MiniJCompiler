package minijava.ast.rules;

import minijava.ast.visitors.ExpVisitor;


public class ExpId extends Exp {

  final public String id;

  public ExpId(String id) {
    this.id = id;
  }

  @Override
  public <A, T extends Throwable> A accept(ExpVisitor<A, T> v) throws T{
    return v.visit(this);
  }
}
