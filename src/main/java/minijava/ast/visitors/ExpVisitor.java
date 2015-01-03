package minijava.ast.visitors;

import minijava.ast.rules.expressions.ExpArrayGet;
import minijava.ast.rules.expressions.ExpArrayLength;
import minijava.ast.rules.expressions.ExpBinOp;
import minijava.ast.rules.expressions.ExpFalse;
import minijava.ast.rules.expressions.ExpId;
import minijava.ast.rules.expressions.ExpIntConst;
import minijava.ast.rules.expressions.ExpInvoke;
import minijava.ast.rules.expressions.ExpNeg;
import minijava.ast.rules.expressions.ExpNew;
import minijava.ast.rules.expressions.ExpNewIntArray;
import minijava.ast.rules.expressions.ExpThis;
import minijava.ast.rules.expressions.ExpTrue;


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


