package minijava.intermediate.tree;

import minijava.intermediate.Temp;
import minijava.intermediate.visitors.TreeExpVisitor;

public class TreeExpTEMP extends TreeExp {
 public final Temp temp;

  public TreeExpTEMP(Temp temp) {
    if (temp == null) {
      throw new NullPointerException();
    }
    this.temp = temp;
  }

  @Override
  public <A, T extends Throwable> A accept(TreeExpVisitor<A, T> visitor) throws T {
    return visitor.visit(this);
  }

  @Override
  public String toString() {
    return "TEMP(" + temp + ")";
  }

}
