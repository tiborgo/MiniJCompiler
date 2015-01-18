package minijava.parse.rules.declarations;

import java.util.Collections;

import minijava.parse.rules.Parameter;
import minijava.parse.rules.expressions.IntConstant;
import minijava.parse.rules.statements.Statement;

public class MainMethod extends Method {
	
	public MainMethod(String argsName, Statement body) {
		
		// FIXME: Main method has argument of type String[]
		super(new minijava.parse.rules.types.Integer(),
				"main",
				Collections.<Parameter>emptyList(),
				Collections.<Variable>emptyList(),
				body,
				new IntConstant(0));
	}
}
