package minijava.parse.rules.statements;

public abstract class Statement {

  public abstract <A, T extends Throwable> A accept(StatementVisitor<A, T> v) throws T;
}

