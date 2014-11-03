package minijava.intermediate.tree;

public abstract class TreeExp {

  public abstract <A> A accept(TreeExpVisitor<A> visitor);
}
