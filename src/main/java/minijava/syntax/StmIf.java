package minijava.syntax;

public class StmIf extends Stm {

  final public Exp cond;
  final public Stm bodyTrue;
  final public Stm bodyFalse;

  public StmIf(Exp cond, Stm bodyTrue, Stm bodyFalse) {
    this.cond = cond;
    this.bodyTrue = bodyTrue;
    this.bodyFalse = bodyFalse;
  }

  @Override
  public <A, T extends Throwable> A accept(StmVisitor<A, T> v) throws T {
    return v.visit(this);
  }
}

