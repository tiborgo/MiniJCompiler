package minijava.intermediate;

public interface FragmentVisitor<A, B> {

  public B visit(FragmentProc<A> fragProc);
}
