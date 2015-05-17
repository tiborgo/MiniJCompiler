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

public class TreeStmCJUMP extends TreeStm {

  public enum Rel {

    EQ("=="), NE("!="), LT("<"), GT(">"), LE("<="), GE(">="),
    ULT("<u"), ULE(">=u"), UGT(">u"), UGE(">=u");
    private final String pretty;

    private Rel(String pretty) {
      this.pretty = pretty;
    }

    public Rel neg() {
      switch (this) {
        case EQ:
          return NE;
        case NE:
          return EQ;
        case LT:
          return GE;
        case GT:
          return LE;
        case LE:
          return GT;
        case GE:
          return LT;
        case ULT:
          return UGE;
        case UGT:
          return ULE;
        case ULE:
          return UGT;
        case UGE:
          return ULT;
        default:
          assert(false);
          return EQ;
      }
    }

    @Override
    public String toString() {
      return pretty;
    }
  }
  public final Rel rel;
  public final TreeExp left, right;
  public final Label ltrue, lfalse;

  public TreeStmCJUMP(Rel rel, TreeExp left, TreeExp right, Label ltrue, Label lfalse) {
    if (rel == null || left == null || right == null || ltrue == null || lfalse == null) {
      throw new NullPointerException();
    }
    this.rel = rel;
    this.left = left;
    this.right = right;
    this.ltrue = ltrue;
    this.lfalse = lfalse;
  }

  public
  @Override
  <A, T extends Throwable> A accept(TreeStmVisitor<A, T> visitor) throws T {
    return visitor.visit(this);
  }

  @Override
  public String toString() {
    return "CJUMP(" + rel + ", " + left + ", " + right +
            ", " + ltrue + ", " + lfalse + ")";
  }
}
