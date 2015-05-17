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
package minij.translate.tree;

import minij.translate.layout.Label;

import java.util.Arrays;
import java.util.List;

public class TreeStmJUMP extends TreeStm {
  public final TreeExp dest;
  public final List<Label> poss;

  public TreeStmJUMP(TreeExp dest, List<Label> poss) {
    if (dest == null || poss == null) {
      throw new NullPointerException();
    }
    this.dest = dest;
    this.poss = poss;
  }

  // factory method for notational convenience
  static public TreeStmJUMP jumpToLabel(Label l) {
    Label[] lSingleton = {l};
    return new TreeStmJUMP(new TreeExpNAME(l), Arrays.asList(lSingleton));
  }

  @Override
  public <A, T extends Throwable> A accept(TreeStmVisitor<A, T> visitor) throws T {
    return visitor.visit(this);
  }

  @Override
  public String toString() {
    String s = "JUMP(" + dest + ", ";
    String sep = "[";
    for (Label l : poss) {
      s += sep + l;
      sep = ", ";
    }
    s += "])";
    return s;
  }

}
