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


public class TreeExpOP extends TreeExp{

    public enum Op {
      PLUS("+"), MINUS("-"), MUL("*"), DIV("/"), AND("&"), OR("|"),
      LSHIFT("<<"), RSHIFT(">>"), ARSHIFT(">|"), XOR("^");
      
      private final String pretty;
      private Op(String pretty) {
        this.pretty = pretty;
      }

    @Override
      public String toString() {
        return pretty;
      }
    }

    public final Op op;
    public final TreeExp left, right;

  public TreeExpOP(Op op, TreeExp left, TreeExp right) {
    if (op == null || left == null || right == null) {
      throw new NullPointerException();
    }
    this.op = op;
    this.left = left;
    this.right = right;
  }

  @Override
  public <A, T extends Throwable> A accept(TreeExpVisitor<A, T> visitor) throws T {
    return visitor.visit(this);
  }

  @Override
  public String toString() {
    return "OP(" + op + ", " + left + ", " + right + ")";
  }

}
