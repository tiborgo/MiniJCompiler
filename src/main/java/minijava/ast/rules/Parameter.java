package minijava.ast.rules;

import minijava.ast.visitors.ParameterVisitor;

public class Parameter {

	final public String id;
	final public Ty ty;

	public Parameter(String id, Ty ty) {
		this.id = id;
		this.ty = ty;
	}

	public <A, T extends Throwable> A accept(ParameterVisitor<A, T> v) throws T {
		return v.visit(this);
	}
}
