package minijava.backend.i386;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import minijava.backend.Assem;
import minijava.backend.DefaultInstruction;
import minijava.backend.i386.visitors.AssemVisitor;
import minijava.intermediate.Label;
import minijava.intermediate.Temp;
import minijava.util.Function;
import minijava.util.Pair;

public final class AssemInstr extends DefaultInstruction {

	public static enum Kind {

		RET, LEAVE, NOP
	}

	public final Kind kind;

	public AssemInstr(Kind kind) {
		this.kind = kind;
	}

	@Override
	public List<Temp> use() {
		switch(kind) {
		case LEAVE:
			return Arrays.asList(I386MachineSpecifics.EBP.reg, I386MachineSpecifics.ESP.reg);
		case RET:
			// callee-save registers: ebx, esi, edi, ebp (ebp already restored by LEAVE)
			// eax is the return value
			return Arrays.asList(I386MachineSpecifics.EBX.reg, I386MachineSpecifics.ESI.reg, I386MachineSpecifics.EDI.reg, I386MachineSpecifics.EAX.reg);
		case NOP:
		default:
			return Collections.emptyList();
		}
	}

	public List<Label> jumps() {
		return Collections.emptyList();
	}

	public boolean isFallThrough() {
		return true;
	}

	public Pair<Temp, Temp> isMoveBetweenTemps() {
		return (kind == Kind.LEAVE) ? new Pair<>(I386MachineSpecifics.EBP.reg, I386MachineSpecifics.ESP.reg) : null;
	}
	
	public Assem rename(Function<Temp, Temp> sigma) {
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
	@Override
	public List<Temp> def() {
		switch(kind) {
		case LEAVE:
			return Arrays.asList(I386MachineSpecifics.EBP.reg, I386MachineSpecifics.ESP.reg);
		case RET:
		case NOP:
		default:
			return Collections.emptyList();
		}
	}

	@Override
	public <A, T extends Throwable> A accept(AssemVisitor<A, T> visitor)
			throws T {
		return visitor.visit(this);
	}
	
	@Override
	public String toString() {
		return this.accept(new I386PrintAssemblyVisitor());
	}
}
