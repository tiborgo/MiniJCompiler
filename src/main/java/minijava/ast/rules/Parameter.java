package minijava.ast.rules;

import minijava.ast.rules.types.Ty;

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

	public static interface DeclVisitor<A, T extends Throwable> {

		public A visit(DeclClass c) throws T;
		public A visit(DeclMain d)  throws T;
		public A visit(DeclMeth m)  throws T;
		public A visit(DeclVar d)   throws T;
	}
}
