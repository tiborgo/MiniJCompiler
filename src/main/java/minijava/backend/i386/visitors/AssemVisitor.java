package minijava.backend.i386.visitors;

import minijava.backend.Directive;
import minijava.backend.Instruction;
import minijava.backend.i386.AssemBinaryOp;
import minijava.backend.i386.AssemInstr;
import minijava.backend.i386.AssemJump;
import minijava.backend.i386.AssemLabel;
import minijava.backend.i386.AssemUnaryOp;


public interface AssemVisitor<A, T extends Throwable> {
	A visit(AssemBinaryOp assem) throws T;
	A visit(AssemInstr assem) throws T;
	A visit(AssemJump assem) throws T;
	A visit(AssemLabel assem) throws T;
	A visit(AssemUnaryOp assem) throws T;
	A visit(Directive assem) throws T;
	A visit(Instruction instruction) throws T;
}
