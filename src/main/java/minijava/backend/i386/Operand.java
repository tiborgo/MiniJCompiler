package minijava.backend.i386;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import minijava.backend.i386.visitors.OperandVisitor;
import minijava.intermediate.Temp;
import minijava.util.Function;

public abstract class Operand {
	
	public abstract <A, T extends Throwable> A accept(OperandVisitor<A, T> visitor) throws T;
	public abstract List<Temp> getTemps();
	public abstract Operand rename(Function<Temp, Temp> sigma);
	
	public final static class Imm extends Operand {

		public final int imm;

		public Imm(Integer imm) {
			assert (imm != null);
			this.imm = imm;
		}

		@Override
		public Operand rename(Function<Temp, Temp> sigma) {
			return this;
		}

		@Override
		public <A, T extends Throwable> A accept(OperandVisitor<A, T> visitor) throws T {
			return visitor.visit(this);
		}

		@Override
		public List<Temp> getTemps() {
			return Collections.emptyList();
		}
	}

	public final static class Reg extends Operand {

		public Temp reg;

		public Reg(Temp reg) {
			assert (reg != null);
			this.reg = reg;
		}

		@Override
		public Operand rename(Function<Temp, Temp> sigma) {
			return new Reg(sigma.apply(reg));
		}
		
		@Override
		public <A, T extends Throwable> A accept(OperandVisitor<A, T> visitor) throws T {
			return visitor.visit(this);
		}

		@Override
		public List<Temp> getTemps() {
			return Arrays.asList(reg);
		}
	}

	public final static class Mem extends Operand {

		public final Temp base; // maybe null
		public final Integer scale; // null or 1, 2, 4 or 8;
		public final Temp index; // maybe null
		public final int displacement;

		public Mem(Temp base, Integer scale, Temp index, int displacement) {
			assert (scale == null || (scale == 1 || scale == 2 || scale == 4 || scale == 8));
			this.base = base;
			this.scale = scale;
			this.index = index;
			this.displacement = displacement;
		}

		public Mem(Temp base) {
			this(base, null, null, 0);
		}

		@Override
		public Operand rename(Function<Temp, Temp> sigma) {
			return new Mem(base != null ? sigma.apply(base) : null, scale,
					index != null ? sigma.apply(index) : null, displacement);
		}
		
		@Override
		public <A, T extends Throwable> A accept(OperandVisitor<A, T> visitor) throws T {
			return visitor.visit(this);
		}

		@Override
		public List<Temp> getTemps() {
			List<Temp> temps = new LinkedList<>();
			if (base != null) temps.add(base);
			if (index != null) temps.add(index);
			return temps;
		}
	}

	public static class Label extends Operand {
		public final minijava.intermediate.Label label;

		public Label(minijava.intermediate.Label label) {
			this.label = label;
		}

		@Override
		public Operand rename(Function<Temp, Temp> sigma) {
			return this;
		}
		
		@Override
		public <A, T extends Throwable> A accept(OperandVisitor<A, T> visitor) throws T {
			return visitor.visit(this);
		}

		@Override
		public List<Temp> getTemps() {
			return Collections.emptyList();
		}
	}
}
