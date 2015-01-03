package minijava.ast.rules.types;

public interface TyVisitor<A, T extends Throwable> {

  public A visit(TyVoid t)  throws T;
  public A visit(TyBool t)  throws T;
  public A visit(TyInt t)   throws T;
  public A visit(TyClass t) throws T;
  public A visit(TyArr t)   throws T;
}
