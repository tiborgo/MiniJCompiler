package minijava.syntax;

public class StmWhile extends Stm {

  final public Exp cond;
  final public Stm body;

  public StmWhile(Exp cond, Stm body) {
    this.cond = cond;
    this.body = body;
  }

  @Override
  public <A, T extends Throwable> A accept(StmVisitor<A, T> v) throws T {
    return v.visit(this);
  }
}

