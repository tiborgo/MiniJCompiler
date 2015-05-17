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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import minij.backend.i386.assems.AssemVisitor;
import minij.backend.i386.assems.Operand;
import minij.translate.layout.Temp;

/**
 * Represents an assembly instruction with an arbitrary number of operands.
 */
public abstract class Instruction implements Assem {
	public final List<Operand> operands;

	public Instruction(Operand... operands) {
		this.operands = new ArrayList<>(Arrays.asList(operands));
	}

	@Override
	public List<Temp> use() {
		ArrayList<Temp> usedTemporaries = new ArrayList<>();
		for (Operand operand : operands) {
			if (operand instanceof Operand.Reg) {
				usedTemporaries.add(((Operand.Reg) operand).reg);
			} else if (operand instanceof Operand.Mem) {
				Operand.Mem memoryAccess = (Operand.Mem) operand;
				if (memoryAccess.base != null) {
					usedTemporaries.add(memoryAccess.base);
				}
				if (memoryAccess.index != null) {
					usedTemporaries.add(memoryAccess.index);
				}
			}
		}
		return usedTemporaries;
	}

	@Override
	public <A, T extends Throwable> A accept(AssemVisitor<A, T> visitor) throws T {
		return visitor.visit(this);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName().toUpperCase();
	}
}
