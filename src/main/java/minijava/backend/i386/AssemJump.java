package minijava.backend.i386;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import minijava.backend.Assem;
import minijava.backend.AssemVisitor;
import minijava.backend.DefaultInstruction;
import minijava.intermediate.Label;
import minijava.intermediate.Temp;
import minijava.util.Function;

public final class AssemJump extends DefaultInstruction {

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
		super(dest);
		assert (kind != Kind.J || cond != null) : "J needs condition argument";
		assert (kind == Kind.CALL || dest instanceof Operand.Label) : "J and JMP need label as destination";
		assert (dest == null || dest instanceof Operand.Reg) : "dynamic destination of CALL must be Reg";
		this.kind = kind;
		this.dest = dest;
		this.cond = cond;
	}

	public List<Temp> use() {
		List<Temp> usedTemporaries = new ArrayList<>(2);
		if (dest instanceof Operand.Reg) {
			usedTemporaries.add(((Operand.Reg) dest).reg);
		} else if (dest instanceof Operand.Mem) {
			Operand.Mem memoryAccess = (Operand.Mem) dest;
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
		return Collections.emptyList();
	}

	public List<Label> jumps() {
		if (kind != Kind.CALL && dest instanceof Operand.Label) {
			return Collections.singletonList(((Operand.Label) dest).label);
		}
		else {
			// TODO: Enable jump to an address given as Operand.Imm
			return Collections.emptyList();
		}
	}

	public boolean isFallThrough() {
		return (kind == Kind.JMP) ? false : true;
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
