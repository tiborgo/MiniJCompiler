package minijava.backend.i386.assems;

import minijava.backend.i386.I386MachineSpecifics;

public class StackAllocation extends AssemBinaryOp {
	public StackAllocation(Operand.Imm byteCount) {
		super(Kind.SUB, I386MachineSpecifics.ESP, byteCount);
	}

	public void setByteCount(Operand.Imm byteCount) {
		this.src = byteCount;
	}
}
