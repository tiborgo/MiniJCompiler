package minijava.ast.rules.statements;

import minijava.ast.rules.expressions.Expression;

public class While extends Statement {

  final public Expression cond;
  final public Statement body;

  public While(Expression cond, Statement body) {
    this.cond = cond;
    this.body = body;
  }

  @Override
  public <A, T extends Throwable> A accept(StatementVisitor<A, T> v) throws T {
    return v.visit(this);
  }
}

