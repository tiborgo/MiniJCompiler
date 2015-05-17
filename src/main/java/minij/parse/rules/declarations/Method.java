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
package minij.parse.rules.declarations;

import java.util.List;

import minij.parse.rules.Parameter;
import minij.parse.rules.expressions.Expression;
import minij.parse.rules.statements.Statement;
import minij.parse.rules.types.Type;

public class Method extends Declaration {

	final public Type type;
	final public String methodName;
	final public List<Parameter> parameters;
	final public List<Variable> localVars;
	final public Statement body;
	final public Expression returnExpression;


	public Method(Type type, String methodName, List<Parameter> parameters,
	              List<Variable> localVars, Statement body, Expression returnExpression) {
		this.type = type;
		this.methodName = methodName;
		this.parameters = parameters;
		this.localVars = localVars;
		this.body = body;
		this.returnExpression = returnExpression;
	}

	public Variable get(String variableName) {
		for (Parameter parameter : parameters) {
			if (parameter.id.equals(variableName)) {
				return new Variable(parameter.type, parameter.id);
			}
		}

		for (Variable variable : localVars) {
			if (variable.name.equals(variableName)) {
				return variable;
			}
		}
		return null;
	}

	@Override
	public <A, T extends Throwable> A accept(DeclarationVisitor<A, T> v) throws T {
		return v.visit(this);
	}
}
