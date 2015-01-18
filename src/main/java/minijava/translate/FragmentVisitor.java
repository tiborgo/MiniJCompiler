package minijava.translate;

public interface FragmentVisitor<A, B> {

  public B visit(FragmentProc<A> fragProc);
}
