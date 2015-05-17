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

import minij.backend.i386.assems.Operand;
import minij.translate.layout.Label;
import minij.translate.layout.Temp;
import minij.util.Function;
import minij.util.Pair;

/**
 * Represents an assembly instruction with an arbitrary number of operands.
 */
public abstract class DefaultInstruction extends Instruction {

	public DefaultInstruction(Operand... operands) {
		super(operands);
	}

	@Override
	public List<Label> jumps() {
		// All instructions except calls/jumps have no jumps
		return Collections.emptyList();
	}

	@Override
	public boolean isFallThrough() {
		// All instructions except calls/jumps are fall-through instructions
		return true;
	}

	@Override
	public final Label isLabel() {
		// Labels are directives, not instructions
		return null;
	}

	@Override
	public List<Temp> def() {
		return Collections.emptyList();
	}

	@Override
	public Pair<Temp, Temp> isMoveBetweenTemps() {
		return null;
	}

	@Override
	public Assem rename(Function<Temp, Temp> sigma) {
		return null;
	}
}
