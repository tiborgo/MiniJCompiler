package minijava.ast.rules.declarations;

import java.util.Collections;

import minijava.ast.rules.Parameter;
import minijava.ast.rules.expressions.IntConstant;
import minijava.ast.rules.statements.Statement;

public class MainMethod extends Method {
	
	public MainMethod(String argsName, Statement body) {
		
		// FIXME: Main method has argument of type String[]
		super(new minijava.ast.rules.types.Integer(),
				"main",
				Collections.<Parameter>emptyList(),
				Collections.<Variable>emptyList(),
				body,
				new IntConstant(0));
	}
}
