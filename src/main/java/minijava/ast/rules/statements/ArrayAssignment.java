package minijava.ast.rules.statements;

import minijava.ast.rules.expressions.Expression;

public class ArrayAssignment extends Statement {

  final public String id;
  final public Expression index;
  final public Expression rhs;

  public ArrayAssignment(String id, Expression index, Expression rhs) {
    this.id = id;
    this.index = index;
    this.rhs = rhs;
  }

  @Override
  public <A, T extends Throwable> A accept(StatementVisitor<A, T> v) throws T {
    return v.visit(this);
  }
}
