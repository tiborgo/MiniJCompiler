package minijava.ast.rules.types;

public abstract class Ty {

  @Override
  public abstract String toString();

  public abstract <A, T extends Throwable> A accept(TyVisitor<A, T> v) throws T;
}




