package minijava.syntax;

public class ExpNew extends Exp {

  final public String className;

  public ExpNew(String className) {
    this.className = className;
  }

  @Override
  public <A, T extends Throwable> A accept(ExpVisitor<A, T> v) throws T {
    return v.visit(this);
  }
}
