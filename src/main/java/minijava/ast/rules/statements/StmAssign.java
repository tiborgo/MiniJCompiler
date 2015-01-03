package minijava.ast.rules.statements;

import minijava.ast.rules.expressions.Exp;
import minijava.ast.visitors.StmVisitor;

public class StmAssign extends Stm {

  final public String id;
  final public Exp rhs;

  public StmAssign(String id, Exp rhs) {
    this.id = id;
    this.rhs = rhs;
  }

  @Override
  public <A, T extends Throwable> A accept(StmVisitor<A, T> v) throws T {
    return v.visit(this);
  }
}
