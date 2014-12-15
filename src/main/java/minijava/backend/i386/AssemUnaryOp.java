package minijava.backend.i386;

import java.util.ArrayList;
import java.util.List;

import minijava.backend.Assem;
import minijava.backend.AssemVisitor;
import minijava.intermediate.Label;
import minijava.intermediate.Temp;
import minijava.util.Function;
import minijava.util.Pair;

public final class AssemUnaryOp implements Assem {

	public static enum Kind {

		PUSH, POP, NEG, NOT, INC, DEC, IMUL, IDIV, ENTER
	}

	public final Operand op;
	public final Kind kind;

	public AssemUnaryOp(Kind kind, Operand op) {
		assert ((kind == Kind.POP || kind == Kind.NEG || kind == Kind.NEG
				|| kind == Kind.INC || kind == Kind.DEC || kind == Kind.IDIV) ? !(op instanceof Operand.Imm)
				: true);
		assert ((kind == Kind.ENTER) ? (op instanceof Operand.Imm) : true);
		this.op = op;
		this.kind = kind;
	}

	public List<Temp> use() {
		ArrayList<Temp> usedTemporaries = new ArrayList<>(2);
		if (op instanceof Operand.Reg) {
			usedTemporaries.add(((Operand.Reg) op).reg);
		} else if (op instanceof Operand.Mem) {
			Operand.Mem memoryAccess = (Operand.Mem) op;
			if (memoryAccess.base != null) {
				usedTemporaries.add(memoryAccess.base);
			}
			if (memoryAccess.index != null) {
				usedTemporaries.add(memoryAccess.index);
			}
		}
		return usedTemporaries;
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
