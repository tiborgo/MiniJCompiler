package minijava.translate.tree;


public abstract class TreeExp {

  public abstract <A, T extends Throwable> A accept(TreeExpVisitor<A, T> visitor) throws T;
}
