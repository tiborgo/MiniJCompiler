package minijava.ast.rules.expressions;

public abstract class Expression {

  public abstract <A, T extends Throwable> A accept(ExpressionVisitor<A, T> v) throws T;
}



