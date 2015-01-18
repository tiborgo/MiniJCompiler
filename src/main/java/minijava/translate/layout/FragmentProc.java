package minijava.translate.layout;

/**
 * Represents a piece of code that stores statements and their respective frame.
 * @param <A> Type of statements that is stored.
 */
public class FragmentProc<A> extends Fragment<A> {

  public final Frame frame;
  public final A body;

  public FragmentProc(Frame frame, A body) {
    this.frame = frame;
    this.body = body;
  }

  @Override
  public String toString() {
    return "Proc(" + frame + ", " + body + ")";
  }

  @Override
  public <B> B accept(FragmentVisitor<A, B> visitor) {
    return visitor.visit(this);
  }
}
