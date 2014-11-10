package minijava.intermediate;

public abstract class Fragment<B> {

  public abstract <A> A accept(FragmentVisitor<B, A> visitor);
}
