package minijava.translate.tree;

import minijava.translate.visitors.TreeExpVisitor;

public abstract class TreeExp {

  public abstract <A, T extends Throwable> A accept(TreeExpVisitor<A, T> visitor) throws T;
}
