package minijava.syntax;

public abstract class Stm {

  public abstract <A, T extends Throwable> A accept(StmVisitor<A, T> v) throws T;

  public String prettyPrint() {
    return accept(new PrettyPrint.PrettyPrintVisitorStm());
  }
}

