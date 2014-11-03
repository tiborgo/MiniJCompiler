package minijava.syntax;

public class TyInt extends Ty {

  public TyInt() {
  }

  @Override
  public String toString() {
    return "int";
  }

  @Override
  public <A> A accept(TyVisitor<A> v) {
    return v.visit(this);
  }

  @Override
  public boolean equals(Object obj) {
    return (obj instanceof TyInt);
  }

  @Override
  public int hashCode() {
    int hash = 5;
    return hash;
  }
}
