package minijava.backend.i386;

import java.util.Collections;
import java.util.List;

import minijava.backend.Assem;
import minijava.backend.AssemVisitor;
import minijava.backend.DefaultInstruction;
import minijava.intermediate.Temp;
import minijava.util.Function;

public final class AssemInstr extends DefaultInstruction {

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

	public Assem rename(Function<Temp, Temp> sigma) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public <A, T extends Throwable> A accept(AssemVisitor<A, T> visitor)
			throws T {
		return visitor.visit(this);
	}
}
