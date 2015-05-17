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
