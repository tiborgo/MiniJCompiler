package minij.backend.i386.assems;

import minij.backend.i386.I386MachineSpecifics;
import minij.instructionselection.assems.Assem;
import minij.translate.layout.Temp;
import minij.util.Function;

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
