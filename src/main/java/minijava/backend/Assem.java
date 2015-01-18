package minijava.backend;

import java.util.List;

import minijava.backend.i386.visitors.AssemVisitor;
import minijava.translate.Label;
import minijava.translate.Temp;
import minijava.util.Function;
import minijava.util.Pair;

// Assumption: is immutable!
public interface Assem {

  /**
   * Returns a list of temporaries that is used by this assembly element.
   * @return Required temporaries
   */
  public List<Temp> use();

  /**
   * Returns a list of temporaries that are defined or redefined by this assembly element.
   * @return Changed temporaries
   */
  public List<Temp> def();

  /**
   * Returns a list of all targets this element can jump to.
   * @return Possible jump targets
   */
  public List<Label> jumps();

  /**
   * Returns whether this element can jump or not.
   * @return {@code true} if the element does not jump, {@code false} otherwise
   */
  public boolean isFallThrough();

  /**
   * Returns the operands that are involved in an assignment of one temporary to another.
   * @return Temporaries that are part of the assignment or {@code null}, if no assignment occurs
   */
  // TODO: Change method signature
  public Pair<Temp, Temp> isMoveBetweenTemps();

  /**
   * Returns the label of this element.
   * @return Element label or {@code null}, if this element has no label.
   */
  // TODO: Change method signature
  public Label isLabel();

  public Assem rename(Function<Temp, Temp> sigma);
  
  public <A, T extends Throwable> A accept(AssemVisitor<A, T> visitor) throws T;
}
