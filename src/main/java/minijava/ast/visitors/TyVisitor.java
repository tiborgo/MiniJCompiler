package minijava.ast.visitors;

import minijava.ast.rules.TyArr;
import minijava.ast.rules.TyBool;
import minijava.ast.rules.TyClass;
import minijava.ast.rules.TyInt;
import minijava.ast.rules.TyVoid;

public interface TyVisitor<A, T extends Throwable> {

  public A visit(TyVoid t)  throws T;
  public A visit(TyBool t)  throws T;
  public A visit(TyInt t)   throws T;
  public A visit(TyClass t) throws T;
  public A visit(TyArr t)   throws T;
}
