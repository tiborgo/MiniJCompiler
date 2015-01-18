package minijava.translate.tree;

import minijava.translate.Label;
import minijava.translate.visitors.TreeStmVisitor;

import java.util.Arrays;
import java.util.List;

public class TreeStmJUMP extends TreeStm {
  public final TreeExp dest;
  public final List<Label> poss;

  public TreeStmJUMP(TreeExp dest, List<Label> poss) {
    if (dest == null || poss == null) {
      throw new NullPointerException();
    }
    this.dest = dest;
    this.poss = poss;
  }

  // factory method for notational convenience
  static public TreeStmJUMP jumpToLabel(Label l) {
    Label[] lSingleton = {l};
    return new TreeStmJUMP(new TreeExpNAME(l), Arrays.asList(lSingleton));
  }

  @Override
  public <A, T extends Throwable> A accept(TreeStmVisitor<A, T> visitor) throws T {
    return visitor.visit(this);
  }

  @Override
  public String toString() {
    String s = "JUMP(" + dest + ", ";
    String sep = "[";
    for (Label l : poss) {
      s += sep + l;
      sep = ", ";
    }
    s += "])";
    return s;
  }

}
