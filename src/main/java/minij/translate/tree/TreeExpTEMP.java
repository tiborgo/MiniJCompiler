package minij.translate.tree;

import minij.translate.layout.Temp;

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
