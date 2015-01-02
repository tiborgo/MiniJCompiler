package minijava.backend.i386.instructions;

import java.util.Collections;
import java.util.List;

import minijava.backend.DefaultInstruction;
import minijava.backend.i386.Operand;
import minijava.intermediate.Temp;

public class Pop extends DefaultInstruction {
	private final Operand operand;

	public Pop(Operand.Reg dst) {
		super(dst);
		this.operand = dst;
	}

	public Pop(Operand.Mem dst) {
		super(dst);
		this.operand = dst;
	}

	@Override
	public List<Temp> def() {
		if (operand instanceof Operand.Reg) {
			return Collections.singletonList(((Operand.Reg) operand).reg);
		}
		return Collections.emptyList();
	}
}
