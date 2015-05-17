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

public interface ExpressionVisitor<A, T extends Throwable> {
	A visit(True e) throws T;

	A visit(False e) throws T;

	A visit(This e) throws T;

	A visit(NewIntArray e) throws T;

	A visit(New e) throws T;

	A visit(Negate e) throws T;

	A visit(BinOp e) throws T;

	A visit(ArrayGet e) throws T;

	A visit(ArrayLength e) throws T;

	A visit(Invoke e) throws T;

	A visit(IntConstant e) throws T;

	A visit(Id e) throws T;
}
