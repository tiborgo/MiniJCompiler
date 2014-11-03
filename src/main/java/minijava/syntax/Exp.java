package minijava.syntax;

public abstract class Exp {

  public abstract <A, T extends Throwable> A accept(ExpVisitor<A, T> v) throws T;

  public String prettyPrint() {
    return accept(new PrettyPrint.PrettyPrintVisitorExp());
  }
}



