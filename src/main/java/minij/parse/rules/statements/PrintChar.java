package minij.parse.rules.statements;

import minij.parse.rules.expressions.Expression;

public class PrintChar extends Statement {

  final public Expression arg;

  public PrintChar(Expression arg) {
    this.arg = arg;
  }

  @Override
  public <A, T extends Throwable> A accept(StatementVisitor<A, T> v) throws T {
    return v.visit(this);
  }
}
