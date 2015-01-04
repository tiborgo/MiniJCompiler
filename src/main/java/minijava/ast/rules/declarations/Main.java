package minijava.ast.rules.declarations;

import java.util.Arrays;
import java.util.Collections;

import minijava.ast.rules.statements.Statement;

public class Main extends Class {

	public final MainMethod mainMethod;

	// TODO: Main method should be passed in constructor
	public Main(String className, String mainArg, Statement mainBody) {
		
		super(className,
				null,
				Collections.<Variable>emptyList(),
				Arrays.<Method>asList(new MainMethod(mainArg, mainBody))
		);
		
		this.mainMethod = (MainMethod) methods.get(0);
	}

	@Override
	public <A, T extends Throwable> A accept(DeclarationVisitor<A, T> v) throws T {
		return v.visit(this);
	}
}
