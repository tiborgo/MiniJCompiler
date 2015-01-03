package minijava.ast.rules.statements;

import minijava.ast.rules.expressions.Exp;

public class StmPrintChar extends Stm {

  final public Exp arg;

  public StmPrintChar(Exp arg) {
    this.arg = arg;
  }

  @Override
  public <A, T extends Throwable> A accept(StmVisitor<A, T> v) throws T {
    return v.visit(this);
  }
}
