/*
 * MiniJCompiler: Compiler for a subset of the Java(R) language
 *
 * (C) Copyright 2014-2015 Tibor Goldschwendt <goldschwendt@cip.ifi.lmu.de>,
 * Michael Seifert <mseifert@error-reports.org>
 *
 * This file is part of MiniJCompiler.
 *
 * MiniJCompiler is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MiniJCompiler is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MiniJCompiler.  If not, see <http://www.gnu.org/licenses/>.
 */
package minij.translate.layout;

import minij.translate.tree.TreeExp;
import minij.translate.tree.TreeStm;

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
