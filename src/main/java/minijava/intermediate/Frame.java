package minijava.intermediate;

import minijava.intermediate.tree.TreeStm;
import minijava.intermediate.tree.TreeExp;

public interface Frame {

  enum Location {

    ANYWHERE, IN_MEMORY
  };

  /**
   * Name of the frame. Typically this will be the name of a function,
   */
  Label getName();

  int getParameterCount();

  /**
   * Returns a tree expression that can be used for reading/writing
   * the n-th parameter in the frame.
   */
  TreeExp getParameter(int number);

  /**
   * Allocates a new local and returns a tree expression by which it
   * can be accessed.
   */
  TreeExp addLocal(Location l);

  /**
   * The frame abstracts how return values are returned to the caller.
   * This method takes a method body and a return expression and returns 
   * a statement that first executes the body and then returns the expression
   * to the caller.
   * 
   * Corresponds to procEntryExit1 in the book.
   */
  TreeStm makeProc(TreeStm body, TreeExp returnValue);

  /**
   * Returns the frame size, i.e. how much actual memory is being used for the 
   * frame.
   */
  int size();

  /**
   * Return a new copy of the frame.
   */
  Frame clone();
}
