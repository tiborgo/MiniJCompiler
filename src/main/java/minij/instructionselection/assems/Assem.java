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
package minij.instructionselection.assems;

import java.util.List;

import minij.backend.i386.assems.AssemVisitor;
import minij.translate.layout.Label;
import minij.translate.layout.Temp;
import minij.util.Function;
import minij.util.Pair;

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
  public Pair<Temp, Temp> isMoveBetweenTemps();

  /**
   * Returns the label of this element.
   * @return Element label or {@code null}, if this element has no label.
   */
  public Label isLabel();

  public Assem rename(Function<Temp, Temp> sigma);
  
  public <A, T extends Throwable> A accept(AssemVisitor<A, T> visitor) throws T;
}
