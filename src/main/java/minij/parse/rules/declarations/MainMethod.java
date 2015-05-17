package minij.parse.rules.declarations;

import java.util.Collections;

import minij.parse.rules.Parameter;
import minij.parse.rules.expressions.IntConstant;
import minij.parse.rules.statements.Statement;

public class MainMethod extends Method {
	
	public MainMethod(String argsName, Statement body) {
		
		// FIXME: Main method has argument of type String[]
		super(new minij.parse.rules.types.Integer(),
				"main",
				Collections.<Parameter>emptyList(),
				Collections.<Variable>emptyList(),
				body,
				new IntConstant(0));
	}
}
