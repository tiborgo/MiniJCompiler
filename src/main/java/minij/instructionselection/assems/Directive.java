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
package minij.instructionselection.assems;

import java.util.Collections;
import java.util.List;

import minij.backend.i386.assems.AssemVisitor;
import minij.translate.layout.Label;
import minij.translate.layout.Temp;
import minij.util.Function;
import minij.util.Pair;

public abstract class Directive implements Assem {

	@Override
	public List<Temp> use() {
		return Collections.emptyList();
	}

	@Override
	public List<Temp> def() {
		return Collections.emptyList();
	}

	@Override
	public List<Label> jumps() {
		return Collections.emptyList();
	}

	@Override
	public boolean isFallThrough() {
		return true;
	}

	@Override
	public Pair<Temp, Temp> isMoveBetweenTemps() {
		return null;
	}

	@Override
	public Assem rename(Function<Temp, Temp> sigma) {
		return this;
	}

	@Override
	public <A, T extends Throwable> A accept(AssemVisitor<A, T> visitor) throws T {
		return visitor.visit(this);
	}
}
