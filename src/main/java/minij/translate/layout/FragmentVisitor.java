package minij.translate.layout;

public interface FragmentVisitor<A, B> {

  public B visit(FragmentProc<A> fragProc);
}
