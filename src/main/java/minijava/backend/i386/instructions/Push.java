package minijava.backend.i386.instructions;

import minijava.backend.DefaultInstruction;
import minijava.backend.i386.Operand;

public class Push extends DefaultInstruction {

	public Push(Operand.Reg src) {
		super(src);
	}

	public Push(Operand.Imm src) {
		super(src);
	}

	public Push(Operand.Mem src) {
		super(src);
	}
}
