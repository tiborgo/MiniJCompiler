package minij.parse.rules;

public interface ProgramVisitor<A, T extends Throwable> {
	A visit(Program p) throws T;
}
