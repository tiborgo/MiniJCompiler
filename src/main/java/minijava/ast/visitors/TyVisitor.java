package minijava.ast.visitors;

import minijava.ast.rules.types.TyArr;
import minijava.ast.rules.types.TyBool;
import minijava.ast.rules.types.TyClass;
import minijava.ast.rules.types.TyInt;
import minijava.ast.rules.types.TyVoid;

public interface TyVisitor<A, T extends Throwable> {

  public A visit(TyVoid t)  throws T;
  public A visit(TyBool t)  throws T;
  public A visit(TyInt t)   throws T;
  public A visit(TyClass t) throws T;
  public A visit(TyArr t)   throws T;
}
