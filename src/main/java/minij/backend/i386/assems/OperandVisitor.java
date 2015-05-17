package minij.backend.i386.assems;


public interface OperandVisitor<A, T extends Throwable> {
	A visit(Operand.Imm operand) throws T;
	A visit(Operand.Label operand) throws T;
	A visit(Operand.Mem operand) throws T;
	A visit(Operand.Reg operand) throws T;
}
