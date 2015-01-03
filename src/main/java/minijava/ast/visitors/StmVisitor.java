package minijava.ast.visitors;

import minijava.ast.rules.statements.StmArrayAssign;
import minijava.ast.rules.statements.StmAssign;
import minijava.ast.rules.statements.StmIf;
import minijava.ast.rules.statements.StmList;
import minijava.ast.rules.statements.StmPrintChar;
import minijava.ast.rules.statements.StmPrintlnInt;
import minijava.ast.rules.statements.StmWhile;

public interface StmVisitor<A, T extends Throwable> {

  public A visit(StmList s)        throws T;
  public A visit(StmIf s)          throws T;
  public A visit(StmWhile s)       throws T;
  public A visit(StmPrintlnInt s)  throws T;
  public A visit(StmPrintChar s)   throws T;
  public A visit(StmAssign s)      throws T;
  public A visit(StmArrayAssign s) throws T;
}
