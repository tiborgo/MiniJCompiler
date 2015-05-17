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
package minij.parse.rules.expressions;

public class BinOp extends Expression {

  public enum Op {

    // note: there is no OR in MiniJ (and actually also no DIV)
    PLUS("+"), MINUS("-"), TIMES("*"), DIV("/"), AND("&&"), LT("<");

    private final String name;

    Op(String name) {
      this.name = name;
    }

    @Override
    public String toString() {
      return name;
    }
  };
  final public Expression left;
  final public Op op;
  final public Expression right;

  public BinOp(Expression e1, Op op, Expression e2) {
    this.left = e1;
    this.op = op;
    this.right = e2;
  }

  @Override
  public <A, T extends Throwable> A accept(ExpressionVisitor<A, T> v) throws T{
    return v.visit(this);
  }
}
