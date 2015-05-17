package minij.parse.rules.expressions;

import minij.parse.rules.types.Type;

public abstract class Expression {
  public Type type;

  public abstract <A, T extends Throwable> A accept(ExpressionVisitor<A, T> v) throws T;
}



