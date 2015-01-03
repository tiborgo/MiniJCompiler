package minijava.ast.rules.expressions;

public class ExpThis extends Exp {

  public ExpThis() {
  }

  @Override
  public <A, T extends Throwable> A accept(ExpVisitor<A, T> v) throws T {
    return v.visit(this);
  }

  public static interface ExpVisitor<A, T extends Throwable> {

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
}
