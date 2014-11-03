package minijava.syntax;

public class TyVoid extends Ty {

  public TyVoid() {
  }

  @Override
  public String toString() {
    return "boolean";
  }

  @Override
  public <A> A accept(TyVisitor<A> v) {
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
