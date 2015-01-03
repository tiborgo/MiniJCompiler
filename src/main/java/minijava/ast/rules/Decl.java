package minijava.ast.rules;

public abstract class Decl {
	public abstract <A, T extends Throwable> A accept(Parameter.DeclVisitor<A, T> v) throws T;
}
