package minijava.backend.i386;

import minijava.backend.AssemVisitor;

public class I386PrintAssemblyVisitor implements
		AssemVisitor<String, RuntimeException> {

	@Override
	public String visit(AssemBinaryOp assem) {
		
		StringBuilder operation = new StringBuilder()
			.append(assem.kind.name())
			.append(" ")
			.append(assem.dst.accept(this))
			.append(",")
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
		
		operation.append(" ")
			.append(assem.dest.accept(this));
		
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
	public String visit(AssemLabel assem) {
		return assem.label.toString();
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
		
		if (op.base != null) {
			operation.append(op.base.toString());
			
		}
		
		if (op.index != null) {
			operation
				.append("+")
				.append(op.index);
		}
		
		operation
			.append("*")
			.append(op.scale)
			.append("+")
			.append(op.displacement);
		
		return operation.toString();
	}
	
	public String visit(Operand.Label op) {
		return op.label.toString();
	}
}
