package minijava.backend.i386.instructions;

import java.util.Collections;
import java.util.List;

import minijava.backend.DefaultInstruction;
import minijava.backend.i386.Operand;
import minijava.intermediate.Temp;

public class Idiv extends DefaultInstruction {
	public Idiv(Operand.Reg dst) {
		// TODO: Add eax to the operator list
		super(dst);
	}

	public Idiv(Operand.Mem dst) {
		// TODO: Add eax to the operator list
		super(dst);
	}

	@Override
	public List<Temp> def() {
		// TODO: Add eax, edx
		return Collections.emptyList();
	}
}
