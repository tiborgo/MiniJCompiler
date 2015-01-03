package minijava.ast.rules.declarations;

import java.util.Arrays;
import java.util.Collections;

import minijava.ast.rules.Parameter;
import minijava.ast.rules.statements.Statement;

public class Main extends Declaration {

	public final String className;
	public final Method mainMethod;
	public final Statement mainBody;

	public Main(String className, String mainArg, Statement mainBody) {
		this.className = className;
		// FIXME: Main method has argument of type String[]
		this.mainMethod = new Method(new minijava.ast.rules.types.Void(), "main",
				Arrays.asList(new Parameter(mainArg, null)), Collections.<Variable>emptyList(), mainBody, null);
		this.mainBody = mainBody;
	}

	@Override
	public <A, T extends Throwable> A accept(DeclarationVisitor<A, T> v) throws T {
		return v.visit(this);
	}
}
