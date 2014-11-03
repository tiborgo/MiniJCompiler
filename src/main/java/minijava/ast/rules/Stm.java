package minijava.ast.rules;

import minijava.ast.visitors.PrettyPrintVisitor;
import minijava.ast.visitors.StmVisitor;

public abstract class Stm {

  public abstract <A, T extends Throwable> A accept(StmVisitor<A, T> v) throws T;

  public String prettyPrint() {
    return accept(new PrettyPrintVisitor.PrettyPrintVisitorStm());
  }
}

