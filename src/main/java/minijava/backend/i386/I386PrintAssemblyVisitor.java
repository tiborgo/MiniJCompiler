package minijava.backend.i386;

import java.util.Iterator;

import minijava.backend.Directive;
import minijava.backend.Instruction;
import minijava.backend.i386.visitors.AssemVisitor;
import minijava.backend.i386.visitors.OperandVisitor;

public class I386PrintAssemblyVisitor implements
		AssemVisitor<String, RuntimeException>,
		OperandVisitor<String, RuntimeException> {

	@Override
	public String visit(AssemBinaryOp assem) {

		StringBuilder operation = new StringBuilder()
			.append(assem.kind.name())
			.append("\t")
			.append(assem.dst.accept(this))
			.append(",\t")
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
			operation.append("\t")
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
			.append("\t")
			.append(assem.op.accept(this));

		return operation.toString();
	}

	@Override
	public String visit(Instruction instruction) throws RuntimeException {
		StringBuilder instructionString = new StringBuilder()
			.append(instruction.toString());
		Iterator<Operand> operandIterator = instruction.operands.iterator();
		if (operandIterator.hasNext()) {
			instructionString.append("\t");
		}
		while (operandIterator.hasNext()) {
			Operand operand = operandIterator.next();
			instructionString.append(operand.accept(this));
			if (operandIterator.hasNext()) {
				instructionString.append(",\t");
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
				.append("+")
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
