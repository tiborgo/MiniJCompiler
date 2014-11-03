package minijava.ast.visitors;

import minijava.ast.rules.TyArr;
import minijava.ast.rules.TyBool;
import minijava.ast.rules.TyClass;
import minijava.ast.rules.TyInt;
import minijava.ast.rules.TyVoid;

public interface TyVisitor<A> {

  public A visit(TyVoid t);

  public A visit(TyBool t);

  public A visit(TyInt t);

  public A visit(TyClass t);

  public A visit(TyArr t);
}
