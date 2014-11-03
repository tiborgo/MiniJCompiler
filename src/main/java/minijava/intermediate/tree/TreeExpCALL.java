package minijava.intermediate.tree;

import minijava.intermediate.Label;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class TreeExpCALL extends TreeExp {

  public final TreeExp func;
  public final List<TreeExp> args;

  public TreeExpCALL(TreeExp func, List<TreeExp> args) {
    if (func == null || args == null) {
      throw new NullPointerException();
    }
    this.func = func;
    this.args = args;
  }

  @Override
  public <A> A accept(TreeExpVisitor<A> visitor) {
    return visitor.visit(this);
  }

  @Override
  public String toString() {
    String s = "CALL(" + func;
    for (TreeExp arg : args) {
      s += ", " + arg;
    }
    s += ")";
    return s;
  }

  // a few factory functions for convenience
  public static TreeExp call(String name) {
    return new TreeExpCALL(new TreeExpNAME(new Label(name)),
            new LinkedList<TreeExp>());
  }

  public static TreeExp call1(String name, TreeExp arg1) {
    TreeExp[] args = {arg1};
    return new TreeExpCALL(new TreeExpNAME(new Label(name)),
            Arrays.asList(args));
  }

  public static TreeExp call2(String name, TreeExp arg1, TreeExp arg2) {
    TreeExp[] args = {arg1, arg2};
    return new TreeExpCALL(new TreeExpNAME(new Label(name)),
            Arrays.asList(args));
  }
}
