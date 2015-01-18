package minijava.backend.i386.assems;

import minijava.instructionselection.assems.Directive;
import minijava.instructionselection.assems.Instruction;


public interface AssemVisitor<A, T extends Throwable> {
	A visit(AssemBinaryOp assem) throws T;
	A visit(AssemInstr assem) throws T;
	A visit(AssemJump assem) throws T;
	A visit(AssemLabel assem) throws T;
	A visit(AssemUnaryOp assem) throws T;
	A visit(Directive assem) throws T;
	A visit(Instruction instruction) throws T;
}
