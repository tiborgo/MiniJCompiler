package minijava.ast.rules;

import java.util.List;

import minijava.ast.visitors.StmVisitor;

public class StmList extends Stm {

  final public List<Stm> stms;

  public StmList(List<Stm> stms) {
    this.stms = stms;
  }

  @Override
  public <A, T extends Throwable> A accept(StmVisitor<A, T> v) throws T {
    return v.visit(this);
  }
}

