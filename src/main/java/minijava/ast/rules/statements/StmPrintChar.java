package minijava.ast.rules.statements;

import minijava.ast.rules.expressions.Expression;

public class StmPrintChar extends Stm {

  final public Expression arg;

  public StmPrintChar(Expression arg) {
    this.arg = arg;
  }

  @Override
  public <A, T extends Throwable> A accept(StmVisitor<A, T> v) throws T {
    return v.visit(this);
  }
}
