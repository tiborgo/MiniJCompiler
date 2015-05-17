package minij.parse.rules.declarations;

import java.util.Arrays;
import java.util.Collections;

import minij.parse.rules.statements.Statement;

public class Main extends Class {

	public final MainMethod mainMethod;

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
