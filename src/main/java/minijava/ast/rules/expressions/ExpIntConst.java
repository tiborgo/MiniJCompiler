package minijava.ast.rules.expressions;


public class ExpIntConst extends Exp {

  final public int value;

  public ExpIntConst(int value) {
    this.value = value;
  }

  @Override
  public <A, T extends Throwable> A accept(ExpThis.ExpVisitor<A, T> v) throws T{
    return v.visit(this);
  }
}
