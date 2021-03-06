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
package minij.backend.i386.assems;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import minij.backend.i386.I386MachineSpecifics;
import minij.backend.i386.visitors.I386PrintAssemblyVisitor;
import minij.instructionselection.assems.Assem;
import minij.instructionselection.assems.DefaultInstruction;
import minij.translate.layout.Label;
import minij.translate.layout.Temp;
import minij.util.Function;
import minij.util.Pair;

public final class AssemInstr extends DefaultInstruction {

	public static enum Kind {

		RET, LEAVE, NOP, CDQ
	}

	public final Kind kind;

	public AssemInstr(Kind kind) {
		this.kind = kind;
	}

	@Override
	public List<Temp> use() {
		switch(kind) {
		case LEAVE:
			// Slide 196 is wrong here,
			// leave is equivalent to
			// mov %esp, %ebp
			// pop %ebp
			return Arrays.asList(I386MachineSpecifics.EBP.reg);
		case RET:
			// eax is the returned value
			// and calle save registers
			return Arrays.asList(I386MachineSpecifics.EAX.reg,
					I386MachineSpecifics.EBX.reg,
					I386MachineSpecifics.ESI.reg,
					I386MachineSpecifics.EDI.reg,
					I386MachineSpecifics.EBP.reg);
		case NOP:
			return Collections.emptyList();
		case CDQ:
			// uses eax to determine sign extend
			return Arrays.asList(I386MachineSpecifics.EAX.reg);
		default:
			throw new UnsupportedOperationException("Unknown operand " + kind);
			
		}
	}

	public List<Label> jumps() {
		return Collections.emptyList();
	}

	public boolean isFallThrough() {
		return true;
	}

	public Pair<Temp, Temp> isMoveBetweenTemps() {
		return (kind == Kind.LEAVE) ? new Pair<>(I386MachineSpecifics.EBP.reg, I386MachineSpecifics.ESP.reg) : null;
	}
	
	public Assem rename(Function<Temp, Temp> sigma) {
		return this;
	}
	
	@Override
	public List<Temp> def() {
		switch(kind) {
		case LEAVE:
			return Arrays.asList(I386MachineSpecifics.EBP.reg, I386MachineSpecifics.ESP.reg);
		case CDQ:
			// extends sign of eax to edx
			return Arrays.asList(I386MachineSpecifics.EDX.reg);
		case RET:
		case NOP:
			return Collections.emptyList();
		default:
			throw new UnsupportedOperationException("Unknown operand " + kind);
		}
	}

	@Override
	public <A, T extends Throwable> A accept(AssemVisitor<A, T> visitor) throws T {
		return visitor.visit(this);
	}
	
	@Override
	public String toString() {
		return this.accept(new I386PrintAssemblyVisitor());
	}
}
