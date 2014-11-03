package minijava.syntax.ast;

public abstract class Ty {

  @Override
  public abstract String toString();

  public abstract <A> A accept(TyVisitor<A> v);
}




