package minijava.ast.rules.statements;

import minijava.ast.rules.expressions.Expression;

public class StmAssign extends Stm {

  final public String id;
  final public Expression rhs;

  public StmAssign(String id, Expression rhs) {
    this.id = id;
    this.rhs = rhs;
  }

  @Override
  public <A, T extends Throwable> A accept(StmVisitor<A, T> v) throws T {
    return v.visit(this);
  }
}
