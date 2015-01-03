package minijava.ast.rules.statements;

import minijava.ast.rules.expressions.Expression;

public class StmWhile extends Stm {

  final public Expression cond;
  final public Stm body;

  public StmWhile(Expression cond, Stm body) {
    this.cond = cond;
    this.body = body;
  }

  @Override
  public <A, T extends Throwable> A accept(StmVisitor<A, T> v) throws T {
    return v.visit(this);
  }
}

