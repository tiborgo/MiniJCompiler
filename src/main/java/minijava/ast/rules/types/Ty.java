package minijava.ast.rules.types;

import minijava.ast.visitors.TyVisitor;

public abstract class Ty {

  @Override
  public abstract String toString();

  public abstract <A, T extends Throwable> A accept(TyVisitor<A, T> v) throws T;
}




