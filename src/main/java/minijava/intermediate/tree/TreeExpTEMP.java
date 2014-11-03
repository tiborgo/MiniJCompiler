package minijava.intermediate.tree;

import minijava.intermediate.Temp;

public class TreeExpTEMP extends TreeExp {
 public final Temp temp;

  public TreeExpTEMP(Temp temp) {
    if (temp == null) {
      throw new NullPointerException();
    }
    this.temp = temp;
  }

  @Override
  public <A> A accept(TreeExpVisitor<A> visitor) {
    return visitor.visit(this);
  }

  @Override
  public String toString() {
    return "TEMP(" + temp + ")";
  }

}
