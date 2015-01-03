package minijava.ast.rules.declarations;

public abstract class Decl {
	public abstract <A, T extends Throwable> A accept(DeclVisitor<A, T> v) throws T;
}
