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
package minij.backend.i386.visitors;

import java.util.Iterator;

import minij.backend.i386.assems.AssemBinaryOp;
import minij.backend.i386.assems.AssemInstr;
import minij.backend.i386.assems.AssemJump;
import minij.backend.i386.assems.AssemLabel;
import minij.backend.i386.assems.AssemUnaryOp;
import minij.backend.i386.assems.AssemVisitor;
import minij.backend.i386.assems.Operand;
import minij.backend.i386.assems.OperandVisitor;
import minij.instructionselection.assems.Directive;
import minij.instructionselection.assems.Instruction;

public class I386PrintAssemblyVisitor implements
		AssemVisitor<String, RuntimeException>,
		OperandVisitor<String, RuntimeException> {

	@Override
	public String visit(AssemBinaryOp assem) {

		StringBuilder operation = new StringBuilder()
			.append(assem.kind.name())
			.append(" ")
			.append(assem.dst.accept(this))
			.append(", ")
			.append(assem.src.accept(this));

		return operation.toString();
	}

	@Override
	public String visit(AssemInstr assem) {
		return assem.kind.name();
	}

	@Override
	public String visit(AssemJump assem) {

		StringBuilder operation = new StringBuilder()
			.append(assem.kind.name());

		if (assem.kind == AssemJump.Kind.J) {
			operation.append(assem.cond.name());
		}

		if (assem.dest instanceof Operand.Label) {
			operation.append(" ")
				.append(assem.dest.accept(this));
		}
		else {
			throw new UnsupportedOperationException("Can only print jumps to labels");
		}

		return operation.toString();
	}

	@Override
	public String visit(AssemUnaryOp assem) {
		StringBuilder operation = new StringBuilder()
			.append(assem.kind.name())
			.append(" ")
			.append(assem.op.accept(this));

		return operation.toString();
	}

	@Override
	public String visit(Instruction instruction) throws RuntimeException {
		StringBuilder instructionString = new StringBuilder()
			.append(instruction.toString());
		Iterator<Operand> operandIterator = instruction.operands.iterator();
		if (operandIterator.hasNext()) {
			instructionString.append(" ");
		}
		while (operandIterator.hasNext()) {
			Operand operand = operandIterator.next();
			instructionString.append(operand.accept(this));
			if (operandIterator.hasNext()) {
				instructionString.append(", ");
			}
		}
		return instructionString.toString();
	}

	@Override
	public String visit(AssemLabel assem) {
		return assem.label.toString() + ":";
	}

	@Override
	public String visit(Directive assem) throws RuntimeException {
		return assem.toString();
	}

	//-----

	@Override
	public String visit(Operand.Imm op) {
		return Integer.toString(op.imm);
	}

	@Override
	public String visit(Operand.Reg op) {
		return op.reg.toString();
	}

	@Override
	public String visit(Operand.Mem op) {
		// base + index * size + displacement
		StringBuilder operation = new StringBuilder();
		operation.append("DWORD PTR [");

		if (op.base != null) {
			operation.append(op.base.toString());
		}

		if (op.index != null) {
			operation
				.append("+")
				.append(op.index);
		}

		if (op.scale != null) {
			operation
				.append("*")
				.append(op.scale);
		}

		if (op.displacement != 0) {
			operation
				.append(op.displacement < 0 ? "" : "+")
				.append(op.displacement);
		}
		operation.append("]");


		return operation.toString();
	}

	@Override
	public String visit(Operand.Label op) {
		return op.label.toString();
	}
}
