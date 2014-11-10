package minijava.ast.rules;

import minijava.ast.visitors.DeclVisitor;

public class DeclVar extends Decl {

	final public Ty ty;
	final public String name;

	public DeclVar(Ty ty, String name) {
		this.ty = ty;
		this.name = name;
	}

	@Override
	public <A, T extends Throwable> A accept(DeclVisitor<A, T> v) throws T {
		return v.visit(this);
	}
}