package minijava.parse.rules.types;

public class Class extends Type {

  final public String c;

  public Class(String c) {
    this.c = c;
  }

  @Override
  public String toString() {
    return c;
  }

  @Override
  public <A, T extends Throwable> A accept(TypeVisitor<A, T> v) throws T {
    return v.visit(this);
  }

  @Override
  public boolean equals(Object obj) {
    return (obj instanceof Class && c.equals(((Class) obj).c));
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 29 * hash + (this.c != null ? this.c.hashCode() : 0);
    return hash;
  }
}
