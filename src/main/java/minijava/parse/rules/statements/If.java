package minijava.parse.rules.statements;

import minijava.parse.rules.expressions.Expression;

public class If extends Statement {

  final public Expression cond;
  final public Statement bodyTrue;
  final public Statement bodyFalse;

  public If(Expression cond, Statement bodyTrue, Statement bodyFalse) {
    this.cond = cond;
    this.bodyTrue = bodyTrue;
    this.bodyFalse = bodyFalse;
  }

  @Override
  public <A, T extends Throwable> A accept(StatementVisitor<A, T> v) throws T {
    return v.visit(this);
  }
}

