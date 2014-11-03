package minijava.ast.visitors;

import minijava.ast.rules.StmArrayAssign;
import minijava.ast.rules.StmAssign;
import minijava.ast.rules.StmIf;
import minijava.ast.rules.StmList;
import minijava.ast.rules.StmPrintChar;
import minijava.ast.rules.StmPrintlnInt;
import minijava.ast.rules.StmWhile;

public interface StmVisitor<A, T extends Throwable> {

  public A visit(StmList s) throws T;

  public A visit(StmIf s) throws T;

  public A visit(StmWhile s) throws T;

  public A visit(StmPrintlnInt s) throws T;

  public A visit(StmPrintChar s) throws T;

  public A visit(StmAssign s) throws T;

  public A visit(StmArrayAssign s) throws T;
}
