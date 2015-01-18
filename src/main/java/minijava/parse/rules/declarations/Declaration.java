package minijava.parse.rules.declarations;

public abstract class Declaration {
	public abstract <A, T extends Throwable> A accept(DeclarationVisitor<A, T> v) throws T;
}
