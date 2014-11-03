package minijava.syntax;

public class TyArr extends Ty {

  final public Ty ty;

  public TyArr(Ty ty) {
    this.ty = ty;
  }

  @Override
  public String toString() {
    return ty + "[]";
  }

  @Override
  public <A> A accept(TyVisitor<A> v) {
    return v.visit(this);
  }

  @Override
  public boolean equals(Object t) {
    return (t instanceof TyArr && ((TyArr)t).ty.equals(ty));
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 97 * hash + (this.ty != null ? this.ty.hashCode() : 0);
    return hash;
  }

}
