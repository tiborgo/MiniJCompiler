package minijava.backend.i386.assems;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import minijava.backend.i386.I386MachineSpecifics;
import minijava.backend.i386.visitors.I386PrintAssemblyVisitor;
import minijava.instructionselection.assems.Assem;
import minijava.instructionselection.assems.DefaultInstruction;
import minijava.translate.layout.Label;
import minijava.translate.layout.Temp;
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
			// Slide 196 is wrong here,
			// leave is equivalent to
			// mov %esp, %ebp
			// pop %ebp
			return Arrays.asList(I386MachineSpecifics.EBP.reg);
		case RET:
			// eax is the returned value
			return Arrays.asList(I386MachineSpecifics.EAX.reg);
		case NOP:
			return Collections.emptyList();
		default:
			throw new UnsupportedOperationException("Unknown operand " + kind);
			
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
		return this;
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
	public <A, T extends Throwable> A accept(AssemVisitor<A, T> visitor) throws T {
		return visitor.visit(this);
	}
	
	@Override
	public String toString() {
		return this.accept(new I386PrintAssemblyVisitor());
	}
}