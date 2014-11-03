package minijava.syntax.ast;

public interface TyVisitor<A> {

  public A visit(TyVoid t);

  public A visit(TyBool t);

  public A visit(TyInt t);

  public A visit(TyClass t);

  public A visit(TyArr t);
}
