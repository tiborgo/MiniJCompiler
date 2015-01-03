package minijava.ast.rules.types;

public class Boolean extends Type {

  public Boolean() {
  }

  @Override
  public String toString() {
    return "boolean";
  }

  @Override
  public boolean equals(Object obj) {
    return (obj instanceof Boolean);
  }

  @Override
  public <A, T extends Throwable> A accept(TypeVisitor<A, T> v) throws T {
    return v.visit(this);
  }

  @Override
  public int hashCode() {
    int hash = 7;
    return hash;
  }
}
