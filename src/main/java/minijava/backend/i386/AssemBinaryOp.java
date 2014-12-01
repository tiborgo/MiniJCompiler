package minijava.backend.i386;

import java.util.List;

import minijava.backend.Assem;
import minijava.backend.AssemVisitor;
import minijava.intermediate.Label;
import minijava.intermediate.Temp;
import minijava.util.Function;
import minijava.util.Pair;

public final class AssemBinaryOp implements Assem {

	public static enum Kind {

		MOV, ADD, SUB, SHL, SHR, SAL, SAR, AND, OR, XOR, TEST, CMP, LEA
	}

	private final Operand src;
	private final Operand dst;
	private final Kind kind;

	public AssemBinaryOp(Kind kind, Operand dst, Operand src) {
		assert (kind != null && src != null && dst != null);
		assert (!((src instanceof Operand.Mem) && (dst instanceof Operand.Mem)));
		assert (kind != Kind.LEA || ((src instanceof Operand.Mem) && (dst instanceof Operand.Reg)));
		this.src = src;
		this.dst = dst;
		this.kind = kind;
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
