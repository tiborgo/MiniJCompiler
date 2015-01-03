package minijava.ast.rules.declarations;

import minijava.ast.rules.types.Ty;

public class Variable extends Declaration {

	final public Ty ty;
	final public String name;

	public Variable(Ty ty, String name) {
		this.ty = ty;
		this.name = name;
	}

	@Override
	public <A, T extends Throwable> A accept(DeclarationVisitor<A, T> v) throws T {
		return v.visit(this);
	}
}
