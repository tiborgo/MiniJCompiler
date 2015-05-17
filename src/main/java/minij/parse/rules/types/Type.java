package minij.parse.rules.types;

public abstract class Type {

  @Override
  public abstract String toString();

  public abstract <A, T extends Throwable> A accept(TypeVisitor<A, T> v) throws T;
}




