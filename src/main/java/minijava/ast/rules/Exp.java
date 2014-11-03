package minijava.ast.rules;

import minijava.ast.visitors.ExpVisitor;
import minijava.ast.visitors.PrettyPrintVisitor;

public abstract class Exp {

  public abstract <A, T extends Throwable> A accept(ExpVisitor<A, T> v) throws T;

  public String prettyPrint() {
    return accept(new PrettyPrintVisitor.PrettyPrintVisitorExp());
  }
}



