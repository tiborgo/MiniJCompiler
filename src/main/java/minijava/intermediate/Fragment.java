package minijava.intermediate;

/**
 * Represents a program piece of a specified type.
 * @param <B> Type of program piece which is represented by this fragment.
 * @see minijava.intermediate.FragmentProc
 */
public abstract class Fragment<B> {

  public abstract <A> A accept(FragmentVisitor<B, A> visitor);
}