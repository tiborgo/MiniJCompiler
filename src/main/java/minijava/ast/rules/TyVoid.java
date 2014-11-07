package minijava.ast.rules;

import minijava.ast.visitors.TyVisitor;

public class TyVoid extends Ty {

  public TyVoid() {
  }

  @Override
  public String toString() {
    return "boolean";
  }

  @Override
  public <A, T extends Throwable> A accept(TyVisitor<A, T> v) throws T {
    return v.visit(this);
  }

  @Override
  public boolean equals(Object obj) {
    return (obj instanceof TyVoid);
  }

  @Override
  public int hashCode() {
    int hash = 7;
    return hash;
  }
}
