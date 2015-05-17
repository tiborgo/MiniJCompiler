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
package minij.backend.i386;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import minij.backend.i386.I386Frame;
import minij.backend.i386.I386MachineSpecifics;
import minij.backend.i386.assems.AssemBinaryOp;
import minij.backend.i386.assems.Operand;
import minij.backend.i386.assems.StackAllocation;
import minij.backend.i386.assems.AssemBinaryOp.Kind;
import minij.instructionselection.assems.Assem;
import minij.translate.layout.Label;
import minij.translate.layout.Temp;

import org.junit.Before;
import org.junit.Test;

public class I386MachineSpecificsTest {
	private static I386MachineSpecifics machineSpecifics;

	@Before
	public void setUp() throws Exception {
		machineSpecifics = new I386MachineSpecifics();
	}

	@Test
	public void testSpill() {
		I386Frame testFrame = new I386Frame(new Label("TestFrame"), 2);
		List<Assem> instructions = new ArrayList<>();
		instructions.add(new StackAllocation(new Operand.Imm(0)));
		Temp toSpill = new Temp();
		Temp toSpill2 = new Temp();
		instructions.add(new AssemBinaryOp(Kind.MOV, new Operand.Reg(toSpill), new Operand.Imm(0)));
		instructions.add(new AssemBinaryOp(Kind.MOV, new Operand.Reg(toSpill2), new Operand.Imm(1)));
		List<Assem> spilledCode = machineSpecifics.spill(
				testFrame, instructions, Arrays.asList(toSpill, toSpill2));
		assertTrue(spilledCode.size() > instructions.size());
		for (Assem assem : spilledCode) {
			if (assem instanceof StackAllocation) {
				Operand dst = ((StackAllocation) assem).src;
				assertEquals(2*I386MachineSpecifics.WORD_SIZE, ((Operand.Imm) dst).imm);
			}
		}
	}
}
