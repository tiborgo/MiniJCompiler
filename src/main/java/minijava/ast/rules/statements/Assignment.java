package minijava.ast.rules.statements;

import minijava.ast.rules.expressions.Expression;

public class Assignment extends Statement {

  final public String id;
  final public Expression rhs;

  public Assignment(String id, Expression rhs) {
    this.id = id;
    this.rhs = rhs;
  }

  @Override
  public <A, T extends Throwable> A accept(StatementVisitor<A, T> v) throws T {
    return v.visit(this);
  }
}
