package minijava.intermediate.tree;

import minijava.intermediate.visitors.TreeExpVisitor;

public abstract class TreeExp {

  public abstract <A, T extends Throwable> A accept(TreeExpVisitor<A, T> visitor) throws T;
}
