package minijava.ast.rules.statements;

import minijava.ast.rules.expressions.Expression;

public class StmIf extends Stm {

  final public Expression cond;
  final public Stm bodyTrue;
  final public Stm bodyFalse;

  public StmIf(Expression cond, Stm bodyTrue, Stm bodyFalse) {
    this.cond = cond;
    this.bodyTrue = bodyTrue;
    this.bodyFalse = bodyFalse;
  }

  @Override
  public <A, T extends Throwable> A accept(StmVisitor<A, T> v) throws T {
    return v.visit(this);
  }
}

