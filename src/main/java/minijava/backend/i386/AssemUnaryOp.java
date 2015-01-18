package minijava.backend.i386;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import minijava.backend.Assem;
import minijava.backend.Instruction;
import minijava.backend.i386.visitors.AssemVisitor;
import minijava.translate.Label;
import minijava.translate.Temp;
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
	
	@Override
	public List<Temp> use() {
		
		switch(kind) {
		
		case POP:
			return Arrays.asList(I386MachineSpecifics.ESP.reg);
			
		case PUSH: {
			List<Temp> temps = new ArrayList<>(op.getTemps());
			temps.add(I386MachineSpecifics.ESP.reg);
			return temps;
		}
			
		case NEG:
		case NOT:
		case INC:
		case DEC:
			return op.getTemps();
			
		case IMUL:
		case IDIV: {
			List<Temp> temps = new ArrayList<>(op.getTemps());
			temps.add(I386MachineSpecifics.EAX.reg);
			return temps;
		}
			
		case ENTER:
			return Arrays.asList(I386MachineSpecifics.ESP.reg, I386MachineSpecifics.EBP.reg);
		}
		
		throw new UnsupportedOperationException("Unknown operand " + kind);
	}

	@Override
	public List<Temp> def() {
		switch(kind) {
		
		case POP: {
			List<Temp> temps = new ArrayList<>(op.getTemps());
			temps.add(I386MachineSpecifics.ESP.reg);
			return temps;
		}
			
		case NEG:
		case NOT:
		case INC:
		case DEC:
			return op.getTemps();
		
		case IMUL:
		case IDIV:
			return Arrays.asList(I386MachineSpecifics.EAX.reg, I386MachineSpecifics.EDX.reg);
			
		case PUSH:
			return Arrays.asList(I386MachineSpecifics.ESP.reg);
			
		case ENTER:
			return Arrays.asList(I386MachineSpecifics.ESP.reg, I386MachineSpecifics.EBP.reg);
		}
		
		throw new UnsupportedOperationException("Unknown operand " + kind);
	}

	@Override
	public List<Label> jumps() {
		return Collections.emptyList();
	}

	@Override
	public boolean isFallThrough() {
		return true;
	}

	@Override
	public Pair<Temp, Temp> isMoveBetweenTemps() {
		return null;
	}

	@Override
	public Label isLabel() {
		return null;
	}

	@Override
	public String toString() {
		return this.accept(new I386PrintAssemblyVisitor());
	}

	@Override
	public Assem rename(Function<Temp, Temp> sigma) {
		return new AssemUnaryOp(kind, op.rename(sigma));
	}

	@Override
	public <A, T extends Throwable> A accept(AssemVisitor<A, T> visitor) throws T {
		return visitor.visit(this);
	}
}
