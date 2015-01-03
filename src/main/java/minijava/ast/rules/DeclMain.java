package minijava.ast.rules;

import minijava.ast.rules.statements.Stm;

public class DeclMain extends Decl {

	final public String className;
	final public String mainArg;
	final public Stm mainBody;

	public DeclMain(String className, String mainArg, Stm mainBody) {
		this.className = className;
		this.mainArg = mainArg;
		this.mainBody = mainBody;
	}

	@Override
	public <A, T extends Throwable> A accept(DeclVisitor<A, T> v) throws T {
		return v.visit(this);
	}
}
