package minijava.backend.i386.instructions;

import java.util.Arrays;
import java.util.List;

import minijava.backend.DefaultInstruction;
import minijava.backend.i386.I386MachineSpecifics;
import minijava.backend.i386.Operand;
import minijava.intermediate.Temp;

public class Imul extends DefaultInstruction {
	public Imul(Operand.Reg dst) {
		// TODO: Add eax to the operator list
		super(dst);
	}

	public Imul(Operand.Mem dst) {
		// TODO: Add eax to the operator list
		super(dst);
	}

	@Override
	public List<Temp> def() {
		return Arrays.asList(I386MachineSpecifics.EAX.reg, I386MachineSpecifics.EDX.reg);
	}
}
