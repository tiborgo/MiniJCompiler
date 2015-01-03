package minijava.ast.rules.statements;

import minijava.ast.rules.expressions.Expression;

public class PrintlnInt extends Statement {

  final public Expression arg;

  public PrintlnInt(Expression arg) {
    this.arg = arg;
  }

  @Override
  public <A, T extends Throwable> A accept(StatementVisitor<A, T> v) throws T {
    return v.visit(this);
  }
}
