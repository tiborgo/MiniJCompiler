package minijava.backend.i386;

import java.util.Collections;
import java.util.List;

import minijava.backend.Assem;
import minijava.backend.i386.visitors.AssemVisitor;
import minijava.intermediate.Label;
import minijava.intermediate.Temp;
import minijava.util.Function;
import minijava.util.Pair;

public final class AssemInstr extends I386Assem {

	public static enum Kind {

		RET, LEAVE, NOP
	}

	public final Kind kind;

	public AssemInstr(Kind kind) {
		this.kind = kind;
	}

	public List<Temp> use() {
		return Collections.emptyList();
	}

	public List<Temp> def() {
		/*
		 * Instructions like RET or LEAVE change esp and ebp.
		 * Those registers do not need to be preserved, because they are saved
		 * and restored by the function prologue and epilogue.
		 */
		return Collections.emptyList();
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

	public Label isLabel() {
		return null;
	}
	
	public Assem rename(Function<Temp, Temp> sigma) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public <A, T extends Throwable> A accept(AssemVisitor<A, T> visitor)
			throws T {
		return visitor.visit(this);
	}
}
