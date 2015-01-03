package minijava.ast.rules.expressions;

import minijava.ast.rules.types.Type;

public abstract class Expression {
  public Type type;

  public abstract <A, T extends Throwable> A accept(ExpressionVisitor<A, T> v) throws T;
}



