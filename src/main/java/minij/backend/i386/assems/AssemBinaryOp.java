/*
 * MiniJCompiler: Compiler for a subset of the Java(R) language
 *
 * (C) Copyright 2014-2015 Tibor Goldschwendt <goldschwendt@cip.ifi.lmu.de>,
 * Michael Seifert <mseifert@error-reports.org>
 *
 * This file is part of MiniJCompiler.
 *
 * MiniJCompiler is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MiniJCompiler is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MiniJCompiler.  If not, see <http://www.gnu.org/licenses/>.
 */
package minij.backend.i386.assems;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import minij.backend.i386.visitors.I386PrintAssemblyVisitor;
import minij.instructionselection.assems.Assem;
import minij.instructionselection.assems.DefaultInstruction;
import minij.translate.layout.Temp;
import minij.util.Function;
import minij.util.Pair;

public class AssemBinaryOp extends DefaultInstruction {

	public static enum Kind {

		MOV, ADD, SUB, SHL, SHR, SAL, SAR, AND, OR, XOR, TEST, CMP, LEA
	}

	// FIXME: Member is non-final due to class StackAllocation
	public Operand src;
	public final Operand dst;
	public final Kind kind;

	public AssemBinaryOp(Kind kind, Operand dst, Operand src) {
		super(dst, src);
		assert (kind != null && src != null && dst != null);
		assert (!((src instanceof Operand.Mem) && (dst instanceof Operand.Mem)));
		assert (kind != Kind.LEA || ((src instanceof Operand.Mem) && (dst instanceof Operand.Reg)));
		assert (!((kind == Kind.ADD || kind == Kind.SUB) && (dst instanceof Operand.Imm || dst instanceof Operand.Label)));
		assert (!(kind == Kind.CMP && (dst instanceof Operand.Imm)));
		this.src = src;
		this.dst = dst;
		this.kind = kind;
	}

	@Override
	public List<Temp> def() {
		switch(kind) {
		case MOV:
		case ADD:
		case SUB:
		case SHL:
		case SHR:
		case SAL:
		case SAR:
		case AND:
		case OR:
		case XOR:
		case LEA:
			return dst.getTemps();

		case CMP:
		case TEST:
			return Collections.emptyList();
		}

		throw new UnsupportedOperationException("Operator " + kind + " is not known");
	}

	@Override
	public List<Temp> use() {
		switch(kind) {
		case ADD:
		case SUB:
		case SHL:
		case SHR:
		case SAL:
		case SAR:
		case AND:
		case OR:
		case XOR:
		case CMP:
		case TEST: {
			List<Temp> temps = new ArrayList<>(src.getTemps());
			temps.addAll(dst.getTemps());
			return temps;
		}

		case MOV: {
			if (dst instanceof Operand.Reg) {
				return src.getTemps();
			}
			else {
				List<Temp> temps = new ArrayList<>(src.getTemps());
				temps.addAll(dst.getTemps());
				return temps;
			}
		}

		case LEA:
			return src.getTemps();
		}

		throw new UnsupportedOperationException("Operator " + kind + " is not known");
	}

	@Override
	public Pair<Temp, Temp> isMoveBetweenTemps() {
		if (dst instanceof Operand.Reg && src instanceof Operand.Reg) {
			return new Pair<>(((Operand.Reg) dst).reg, ((Operand.Reg) src).reg);
		}
		return null;
	}

	@Override
	public String toString() {
		return this.accept(new I386PrintAssemblyVisitor());
	}

	@Override
	public Assem rename(Function<Temp, Temp> sigma) {
		return new AssemBinaryOp(kind, dst.rename(sigma), src.rename(sigma));
	}

	@Override
	public <A, T extends Throwable> A accept(AssemVisitor<A, T> visitor) throws T {
		return visitor.visit(this);
	}
}
