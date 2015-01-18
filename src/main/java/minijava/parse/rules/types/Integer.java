package minijava.parse.rules.types;

public class Integer extends Type {

  public Integer() {
  }

  @Override
  public String toString() {
    return "int";
  }

  @Override
  public <A, T extends Throwable> A accept(TypeVisitor<A, T> v) throws T {
    return v.visit(this);
  }

  @Override
  public boolean equals(Object obj) {
    return (obj instanceof Integer);
  }

  @Override
  public int hashCode() {
    int hash = 5;
    return hash;
  }
}
