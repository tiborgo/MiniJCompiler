package minijava.syntax;

import java.util.List;

public class ExpInvoke extends Exp {

  final public Exp obj;
  final public String method;
  final public List<Exp> args;

  public ExpInvoke(Exp obj, String method, List<Exp> args) {
    this.obj = obj;
    this.method = method;
    this.args = args;
  }

  @Override
  public <A, T extends Throwable> A accept(ExpVisitor<A, T> v) throws T{
    return v.visit(this);
  }
}
