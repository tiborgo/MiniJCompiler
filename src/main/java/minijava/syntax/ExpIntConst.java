package minijava.syntax;


public class ExpIntConst extends Exp {

  final public int value;

  public ExpIntConst(int value) {
    this.value = value;
  }

  @Override
  public <A, T extends Throwable> A accept(ExpVisitor<A, T> v) throws T{
    return v.visit(this);
  }
}
