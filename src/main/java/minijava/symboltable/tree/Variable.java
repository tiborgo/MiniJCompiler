package minijava.symboltable.tree;

import minijava.ast.rules.types.Ty;

public class Variable implements Node {

	public final String name;
	public final Ty type;
	
	public Variable(String name, Ty type) {
		this.name = name;
		this.type = type;
	}
}
