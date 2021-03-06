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
package minij.parse.rules.statements;

import minij.parse.rules.expressions.Expression;
import minij.parse.rules.expressions.Id;

public class Assignment extends Statement {

	public final Id id;
	public final Expression rhs;

	public Assignment(Id id, Expression rhs) {
		this.id = id;
		this.rhs = rhs;
	}

	@Override
	public <A, T extends Throwable> A accept(StatementVisitor<A, T> v) throws T {
		return v.visit(this);
	}
}
