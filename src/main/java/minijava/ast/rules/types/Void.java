package minijava.ast.rules.types;

public class Void extends Type {

  public Void() {
  }

  @Override
  public String toString() {
    return "boolean";
  }

  @Override
  public <A, T extends Throwable> A accept(TypeVisitor<A, T> v) throws T {
    return v.visit(this);
  }

  @Override
  public boolean equals(Object obj) {
    return (obj instanceof Void);
  }

  @Override
  public int hashCode() {
    int hash = 7;
    return hash;
  }
}
