package minijava.syntax;

public class TyClass extends Ty {

  final public String c;

  public TyClass(String c) {
    this.c = c;
  }

  @Override
  public String toString() {
    return c;
  }

  @Override
  public <A> A accept(TyVisitor<A> v) {
    return v.visit(this);
  }

  @Override
  public boolean equals(Object obj) {
    return (obj instanceof TyClass && c.equals(((TyClass) obj).c));
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 29 * hash + (this.c != null ? this.c.hashCode() : 0);
    return hash;
  }
}
