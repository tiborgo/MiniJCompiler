package minijava.ast.rules;

import minijava.ast.visitors.StmVisitor;

public class StmPrintlnInt extends Stm {

  final public Exp arg;

  public StmPrintlnInt(Exp arg) {
    this.arg = arg;
  }

  @Override
  public <A, T extends Throwable> A accept(StmVisitor<A, T> v) throws T {
    return v.visit(this);
  }
}
