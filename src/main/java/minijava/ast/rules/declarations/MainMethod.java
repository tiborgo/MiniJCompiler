package minijava.ast.rules.declarations;

import java.util.Arrays;
import java.util.Collections;

import minijava.ast.rules.Parameter;
import minijava.ast.rules.expressions.IntConstant;
import minijava.ast.rules.statements.Statement;

public class MainMethod extends Method {
	
	public MainMethod(String argsName, Statement body) {
		
		// FIXME: Main method has argument of type String[]
		super(new minijava.ast.rules.types.Void(),
				"main",
				Arrays.asList(new Parameter(argsName, null)),
				Collections.<Variable>emptyList(),
				body,
				new IntConstant(0));
	}
}
