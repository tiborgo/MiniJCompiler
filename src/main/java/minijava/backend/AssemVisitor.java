package minijava.backend;


public interface AssemVisitor<A, T extends Throwable> {
	A visit(Assem assem) throws T;
}
