package minijava.ast.rules.declarations;

import java.util.Arrays;
import java.util.Collections;

import minijava.ast.rules.Parameter;
import minijava.ast.rules.expressions.IntConstant;
import minijava.ast.rules.statements.Statement;

public class Main extends Class {

	public final Method mainMethod;

	// TODO: Main method should be passed in constructor
	public Main(String className, String mainArg, Statement mainBody) {
		super(className,
				null,
				Collections.<Variable>emptyList(),
				// FIXME: Main method has argument of type String[]
				Arrays.asList(new Method(new minijava.ast.rules.types.Void(), "main",
					Arrays.asList(new Parameter(mainArg, null)), Collections.<Variable>emptyList(), mainBody, new IntConstant(0))
				)
		);
		this.mainMethod = methods.get(0);
	}

	@Override
	public <A, T extends Throwable> A accept(DeclarationVisitor<A, T> v) throws T {
		return v.visit(this);
	}
}
