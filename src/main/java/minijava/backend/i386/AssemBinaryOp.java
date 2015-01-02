package minijava.backend.i386;


import java.util.List;

import minijava.backend.Assem;
import minijava.backend.AssemVisitor;
import minijava.backend.DefaultInstruction;
import minijava.intermediate.Temp;
import minijava.util.Function;
import minijava.util.Pair;

public final class AssemBinaryOp extends DefaultInstruction {

	public static enum Kind {

		MOV, ADD, SUB, SHL, SHR, SAL, SAR, AND, OR, XOR, TEST, CMP, LEA
	}

	public final Operand src;
	public final Operand dst;
	public final Kind kind;

	public AssemBinaryOp(Kind kind, Operand dst, Operand src) {
		super(dst, src);
		assert (kind != null && src != null && dst != null);
		assert (!((src instanceof Operand.Mem) && (dst instanceof Operand.Mem)));
		assert (kind != Kind.LEA || ((src instanceof Operand.Mem) && (dst instanceof Operand.Reg)));
		this.src = src;
		this.dst = dst;
		this.kind = kind;
	}

	public List<Temp> def() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public Pair<Temp, Temp> isMoveBetweenTemps() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public String toString() {
		return kind.toString();
	}

	public Assem rename(Function<Temp, Temp> sigma) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public <A, T extends Throwable> A accept(AssemVisitor<A, T> visitor) throws T {
		return visitor.visit(this);
	}
}
