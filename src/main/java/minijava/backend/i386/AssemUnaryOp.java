package minijava.backend.i386;

import java.util.List;

import minijava.backend.Assem;
import minijava.backend.AssemVisitor;
import minijava.backend.Instruction;
import minijava.intermediate.Label;
import minijava.intermediate.Temp;
import minijava.util.Function;
import minijava.util.Pair;

public final class AssemUnaryOp extends Instruction {

	public static enum Kind {

		PUSH, POP, NEG, NOT, INC, DEC, IMUL, IDIV, ENTER
	}

	public final Operand op;
	public final Kind kind;

	public AssemUnaryOp(Kind kind, Operand op) {
		super(op);
		assert ((kind == Kind.POP || kind == Kind.NEG || kind == Kind.NEG
				|| kind == Kind.INC || kind == Kind.DEC || kind == Kind.IDIV) ? !(op instanceof Operand.Imm)
				: true);
		assert ((kind == Kind.ENTER) ? (op instanceof Operand.Imm) : true);
		this.op = op;
		this.kind = kind;
	}

	public List<Temp> def() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public List<Label> jumps() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public boolean isFallThrough() {
		return true;
	}

	public Pair<Temp, Temp> isMoveBetweenTemps() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public Label isLabel() {
		return null;
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
