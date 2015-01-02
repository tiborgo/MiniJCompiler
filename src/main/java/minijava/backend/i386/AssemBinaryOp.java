package minijava.backend.i386;


import java.util.Arrays;
import java.util.Collections;
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

	@Override
	public List<Temp> def() {
		if (kind != Kind.CMP && dst instanceof Operand.Reg) {
			return Collections.singletonList(((Operand.Reg) dst).reg);
		}
		return Collections.emptyList();
	}

	@Override
	public Pair<Temp, Temp> isMoveBetweenTemps() {
		if (dst instanceof Operand.Reg && src instanceof Operand.Reg) {
			return new Pair(((Operand.Reg) dst).reg, ((Operand.Reg) src).reg);
		}
		return null;
	}

	@Override
	public String toString() {
		return kind.toString();
	}

	@Override
	public Assem rename(Function<Temp, Temp> sigma) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public <A, T extends Throwable> A accept(AssemVisitor<A, T> visitor) throws T {
		return visitor.visit(this);
	}
}
