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
import java.util.LinkedList;
import java.util.List;

public class TreeExpCALL extends TreeExp {

  public final TreeExp func;
  public final List<TreeExp> args;

  public TreeExpCALL(TreeExp func, List<TreeExp> args) {
    if (func == null || args == null) {
      throw new NullPointerException();
    }
    this.func = func;
    this.args = args;
  }

  @Override
  public <A, T extends Throwable> A accept(TreeExpVisitor<A, T> visitor) throws T {
    return visitor.visit(this);
  }

  @Override
  public String toString() {
    String s = "CALL(" + func;
    for (TreeExp arg : args) {
      s += ", " + arg;
    }
    s += ")";
    return s;
  }

  // a few factory functions for convenience
  public static TreeExp call(String name) {
    return new TreeExpCALL(new TreeExpNAME(new Label(name)),
            new LinkedList<TreeExp>());
  }

  public static TreeExp call1(String name, TreeExp arg1) {
    TreeExp[] args = {arg1};
    return new TreeExpCALL(new TreeExpNAME(new Label(name)),
            Arrays.asList(args));
  }

  public static TreeExp call2(String name, TreeExp arg1, TreeExp arg2) {
    TreeExp[] args = {arg1, arg2};
    return new TreeExpCALL(new TreeExpNAME(new Label(name)),
            Arrays.asList(args));
  }
}
