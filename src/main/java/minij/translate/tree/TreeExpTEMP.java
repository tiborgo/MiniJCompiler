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

import minij.translate.layout.Temp;

public class TreeExpTEMP extends TreeExp {
 public final Temp temp;

  public TreeExpTEMP(Temp temp) {
    if (temp == null) {
      throw new NullPointerException();
    }
    this.temp = temp;
  }

  @Override
  public <A, T extends Throwable> A accept(TreeExpVisitor<A, T> visitor) throws T {
    return visitor.visit(this);
  }

  @Override
  public String toString() {
    return "TEMP(" + temp + ")";
  }

}
