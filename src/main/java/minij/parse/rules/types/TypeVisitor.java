package minij.parse.rules.types;

public interface TypeVisitor<A, T extends Throwable> {

  public A visit(Void t)  throws T;
  public A visit(Boolean t)  throws T;
  public A visit(Integer t)   throws T;
  public A visit(Class t) throws T;
  public A visit(Array t)   throws T;
}
