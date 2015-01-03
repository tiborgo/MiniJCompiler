package minijava.ast.rules.expressions;


public class ExpId extends Exp {

  final public String id;

  public ExpId(String id) {
    this.id = id;
  }

  @Override
  public <A, T extends Throwable> A accept(ExpThis.ExpVisitor<A, T> v) throws T{
    return v.visit(this);
  }
}
