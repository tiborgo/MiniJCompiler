package minijava.backend.i386.assems;

import minijava.backend.i386.I386MachineSpecifics;
import minijava.instructionselection.assems.Assem;
import minijava.translate.layout.Temp;
import minijava.util.Function;

public class StackAllocation extends AssemBinaryOp {
	public StackAllocation(Operand.Imm byteCount) {
		super(Kind.SUB, I386MachineSpecifics.ESP, byteCount);
	}

	public void setByteCount(Operand.Imm byteCount) {
		this.src = byteCount;
	}
	
	@Override
	public Assem rename(Function<Temp, Temp> sigma) {
		return this;
	}
}
