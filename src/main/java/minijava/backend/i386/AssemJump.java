package minijava.backend.i386;

import java.util.List;

import minijava.backend.Assem;
import minijava.backend.AssemVisitor;
import minijava.intermediate.Label;
import minijava.intermediate.Temp;
import minijava.util.Function;
import minijava.util.Pair;

public final class AssemJump implements Assem {

	public enum Kind {

		JMP, J, CALL
	}

	public enum Cond {

		E, NE, L, LE, G, GE, Z
	}

	public final Kind kind;
	public final Operand dest;
	public final Cond cond;

	public AssemJump(Kind kind, Operand dest) {
		this(kind, dest, null);
	}

	public AssemJump(Kind kind, Operand dest, Cond cond) {
		assert (kind != Kind.J || cond != null) : "J needs condition argument";
		assert (kind == Kind.CALL || dest instanceof Operand.Label) : "J and JMP need label as destination";
		assert (dest == null || dest instanceof Operand.Reg) : "dynamic destination of CALL must be Reg";
		this.kind = kind;
		this.dest = dest;
		this.cond = cond;
	}

	public List<Temp> use() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public List<Temp> def() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public List<Label> jumps() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public boolean isFallThrough() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public Pair<Temp, Temp> isMoveBetweenTemps() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public Label isLabel() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public String toString() {
		throw new UnsupportedOperationException("Not supported yet.");
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
