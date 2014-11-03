package minijava.ast.rules;

import minijava.ast.visitors.TyVisitor;

public abstract class Ty {

  @Override
  public abstract String toString();

  public abstract <A> A accept(TyVisitor<A> v);
}




