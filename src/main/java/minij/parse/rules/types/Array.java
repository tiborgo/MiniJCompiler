package minij.parse.rules.types;

public class Array extends Type {

  final public Type type;

  public Array(Type type) {
    this.type = type;
  }

  @Override
  public String toString() {
    return type + "[]";
  }

  @Override
  public <A, T extends Throwable> A accept(TypeVisitor<A, T> v) throws T {
    return v.visit(this);
  }

  @Override
  public boolean equals(Object t) {
    return (t instanceof Array && ((Array)t).type.equals(type));
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 97 * hash + (this.type != null ? this.type.hashCode() : 0);
    return hash;
  }

}
