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
package minij.parse.rules;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import minij.parse.rules.declarations.Class;

public class Program {
	private final Map<String, Class> classes;

	public Program(List<Class> classes) {

		this.classes = new HashMap<>(classes.size());
		for (Class clazz : classes) {
			this.classes.put(clazz.className, clazz);
		}
	}

	public <A, T extends Throwable> A accept(ProgramVisitor<A, T> v) throws T {
		return v.visit(this);
	}

	public Collection<Class> getClasses() {
		return Collections.unmodifiableCollection(classes.values());
	}

	public Class get(String className) {
		return classes.get(className);
	}

	public boolean contains(String className) {
		return classes.containsKey(className);
	}
}
