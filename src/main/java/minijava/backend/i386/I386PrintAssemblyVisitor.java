package minijava.backend.i386;

import minijava.backend.AssemVisitor;
import minijava.backend.Directive;

public class I386PrintAssemblyVisitor implements
		AssemVisitor<String, RuntimeException> {

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
	public String visit(AssemLabel assem) {
		return assem.label.toString() + ":";
	}

	@Override
	public String visit(Directive assem) throws RuntimeException {
		return assem.toString();
	}

	//-----

	public String visit(Operand.Imm op) {
		return Integer.toString(op.imm);
	}

	public String visit(Operand.Reg op) {
		return "%" + op.reg.toString();
	}

	public String visit(Operand.Mem op) {
		// base + index * size + displacement
		StringBuilder operation = new StringBuilder();
		operation.append("DWORD PTR [");

		if (op.base != null) {
			operation.append("%")
				.append(op.base.toString());
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

	public String visit(Operand.Label op) {
		return op.label.toString();
	}
}
