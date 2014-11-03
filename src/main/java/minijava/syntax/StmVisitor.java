package minijava.syntax;

public interface StmVisitor<A, T extends Throwable> {

  public A visit(StmList s) throws T;

  public A visit(StmIf s) throws T;

  public A visit(StmWhile s) throws T;

  public A visit(StmPrintlnInt s) throws T;

  public A visit(StmPrintChar s) throws T;

  public A visit(StmAssign s) throws T;

  public A visit(StmArrayAssign s) throws T;
}
