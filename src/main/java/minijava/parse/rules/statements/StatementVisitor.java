package minijava.parse.rules.statements;

public interface StatementVisitor<A, T extends Throwable> {

  public A visit(StatementList s)        throws T;
  public A visit(If s)          throws T;
  public A visit(While s)       throws T;
  public A visit(PrintlnInt s)  throws T;
  public A visit(PrintChar s)   throws T;
  public A visit(Assignment s)      throws T;
  public A visit(ArrayAssignment s) throws T;
}
