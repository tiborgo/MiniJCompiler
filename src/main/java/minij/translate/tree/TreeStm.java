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

import java.util.List;

import minij.translate.layout.Label;

public abstract class TreeStm {

  public abstract <A, T extends Throwable> A accept(TreeStmVisitor<A, T> visitor)  throws T;

  // construct single TreeStm from a sequence of TreeStms
  public static TreeStm fromArray(TreeStm... stms) {
    TreeStm s = null;
    for (int i = stms.length - 1; i >= 0; i--) {
      s = (s == null) ? stms[i] : new TreeStmSEQ(stms[i], s);
    }
    // no statement?
    if (s == null) {
      s = TreeStm.getNOP();
    }
    return s;
  }

  public static TreeStm fromList(List<TreeStm> stms) {
    TreeStm[] stmArray = new TreeStm[stms.size()];
    stms.toArray(stmArray);
    return TreeStm.fromArray(stmArray);
  }

  public static TreeStm getNOP() {
    return new TreeStmLABEL(new Label());
  }
  
}
