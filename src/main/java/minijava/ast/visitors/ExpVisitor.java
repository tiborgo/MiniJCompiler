package minijava.ast.visitors;

import minijava.ast.rules.ExpArrayGet;
import minijava.ast.rules.ExpArrayLength;
import minijava.ast.rules.ExpBinOp;
import minijava.ast.rules.ExpFalse;
import minijava.ast.rules.ExpId;
import minijava.ast.rules.ExpIntConst;
import minijava.ast.rules.ExpInvoke;
import minijava.ast.rules.ExpNeg;
import minijava.ast.rules.ExpNew;
import minijava.ast.rules.ExpNewIntArray;
import minijava.ast.rules.ExpThis;
import minijava.ast.rules.ExpTrue;


public interface ExpVisitor<A, T extends Throwable> {

  public A visit(ExpTrue e)        throws T;
  public A visit(ExpFalse e)       throws T;
  public A visit(ExpThis e)        throws T;
  public A visit(ExpNewIntArray e) throws T;
  public A visit(ExpNew e)         throws T;
  public A visit(ExpNeg e)         throws T;
  public A visit(ExpBinOp e)       throws T;
  public A visit(ExpArrayGet e)    throws T;
  public A visit(ExpArrayLength e) throws T;
  public A visit(ExpInvoke e)      throws T;
  public A visit(ExpIntConst e)    throws T;
  public A visit(ExpId e)          throws T;
}


