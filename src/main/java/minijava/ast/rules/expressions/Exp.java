package minijava.ast.rules.expressions;

public abstract class Exp {

  public abstract <A, T extends Throwable> A accept(ExpThis.ExpVisitor<A, T> v) throws T;
}



