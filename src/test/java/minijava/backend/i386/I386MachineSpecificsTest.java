package minijava.backend.i386;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import minijava.backend.Assem;
import minijava.backend.i386.AssemBinaryOp.Kind;
import minijava.backend.i386.Operand.Imm;
import minijava.backend.i386.Operand.Reg;
import minijava.translate.layout.Label;
import minijava.translate.layout.Temp;

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
