package minijava.syntax;

public class ExpNewIntArray extends Exp {

  final public Exp size;

  public ExpNewIntArray(Exp size) {
    this.size = size;
  }

  @Override
  public <A, T extends Throwable> A accept(ExpVisitor<A, T> v) throws T {
    return v.visit(this);
  }
}
