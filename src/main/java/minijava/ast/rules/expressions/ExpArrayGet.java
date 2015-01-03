package minijava.ast.rules.expressions;

public class ExpArrayGet extends Exp {

  final public Exp array;
  final public Exp index;

  public ExpArrayGet(Exp array, Exp index) {
    this.array = array;
    this.index = index;
  }

  @Override
  public <A, T extends Throwable> A accept(ExpThis.ExpVisitor<A, T> v) throws T {
    return v.visit(this);
  }
}
