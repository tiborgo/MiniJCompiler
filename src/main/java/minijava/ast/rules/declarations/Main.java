package minijava.ast.rules.declarations;

import minijava.ast.rules.statements.Stm;

public class Main extends Declaration {

	final public String className;
	final public String mainArg;
	final public Stm mainBody;

	public Main(String className, String mainArg, Stm mainBody) {
		this.className = className;
		this.mainArg = mainArg;
		this.mainBody = mainBody;
	}

	@Override
	public <A, T extends Throwable> A accept(DeclarationVisitor<A, T> v) throws T {
		return v.visit(this);
	}
}
