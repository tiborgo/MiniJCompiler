package minijava.ast.rules;

public interface PrgVisitor<A, T extends Throwable> {
	A visit(Prg p) throws T;
}
