package minijava.ast.rules.declarations;

import minijava.ast.rules.statements.Statement;

public class Main extends Declaration {

	final public String className;
	final public String mainArg;
	final public Statement mainBody;

	public Main(String className, String mainArg, Statement mainBody) {
		this.className = className;
		this.mainArg = mainArg;
		this.mainBody = mainBody;
	}

	@Override
	public <A, T extends Throwable> A accept(DeclarationVisitor<A, T> v) throws T {
		return v.visit(this);
	}
}
