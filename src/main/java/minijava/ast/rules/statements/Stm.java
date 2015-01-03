package minijava.ast.rules.statements;

import minijava.ast.visitors.StmVisitor;

public abstract class Stm {

  public abstract <A, T extends Throwable> A accept(StmVisitor<A, T> v) throws T;
}

