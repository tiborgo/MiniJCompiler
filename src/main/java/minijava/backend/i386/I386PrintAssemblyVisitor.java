package minijava.backend.i386;

import minijava.backend.Assem;
import minijava.backend.AssemVisitor;

public class I386PrintAssemblyVisitor implements
		AssemVisitor<String, RuntimeException> {

	@Override
	public String visit(Assem assem) throws RuntimeException {
		// TODO Auto-generated method stub
		return null;
	}

	public String visit(AssemBinaryOp assem) {
		// TODO implement
		return null;
	}
	
	public String visit(AssemInstr assem) {
		// TODO implement
		return null;
	}
	
	public String visit(AssemJump assem) {
		// TODO implement
		return null;
	}
	
	public String visit(AssemUnaryOp assem) {
		// TODO implement
		return null;
	}
}
