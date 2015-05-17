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

public class Class extends Declaration {

  final public String className;
  final public String superName; // null if no superclass
  final public List<Variable> fields;
  final public List<Method> methods;

  public Class(String className, String superName,
               List<Variable> fields, List<Method> methods) {
    this.className = className;
    this.superName = superName;
    this.fields = fields;
    this.methods = methods;
  }

	@Override
	public <A, T extends Throwable> A accept(DeclarationVisitor<A, T> v) throws T {
		return v.visit(this);
	}

  public Method getMethod(String methodName) {
    for (Method declaredMethod : methods) {
      if (declaredMethod.methodName.equals(methodName)) {
        return declaredMethod;
      }
    }
    return null;
  }

  public Variable getField(String fieldName) {
    for (Variable declaredField : fields) {
      if (declaredField.name.equals(fieldName)) {
        return declaredField;
      }
    }
    return null;
  }
}
