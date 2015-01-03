package minijava.backend.i386.visitors;

import minijava.backend.i386.Operand;

public interface OperandVisitor<A, T extends Throwable> {
	A visit(Operand.Imm operand) throws T;
	A visit(Operand.Label operand) throws T;
	A visit(Operand.Mem operand) throws T;
	A visit(Operand.Reg operand) throws T;
}
