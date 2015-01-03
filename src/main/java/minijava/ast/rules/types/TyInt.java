package minijava.ast.rules.types;

public class TyInt extends Ty {

  public TyInt() {
  }

  @Override
  public String toString() {
    return "int";
  }

  @Override
  public <A, T extends Throwable> A accept(TyVisitor<A, T> v) throws T {
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
