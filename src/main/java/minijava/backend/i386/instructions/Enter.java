package minijava.backend.i386.instructions;

import minijava.backend.DefaultInstruction;
import minijava.backend.i386.Operand;

public class Enter extends DefaultInstruction {
	public Enter(Operand.Imm dst) {
		super(dst);
	}
}
