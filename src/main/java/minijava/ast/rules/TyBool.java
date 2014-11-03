package minijava.ast.rules;

import minijava.ast.visitors.TyVisitor;

public class TyBool extends Ty {

  public TyBool() {
  }

  @Override
  public String toString() {
    return "boolean";
  }

  @Override
  public boolean equals(Object obj) {
    return (obj instanceof TyBool);
  }

  @Override
  public <A> A accept(TyVisitor<A> v) {
    return v.visit(this);
  }

  @Override
  public int hashCode() {
    int hash = 7;
    return hash;
  }
}
