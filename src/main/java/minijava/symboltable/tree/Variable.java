package minijava.symboltable.tree;

import minijava.ast.rules.types.Type;

public class Variable implements Node {

	public final String name;
	public final Type type;
	
	public Variable(String name, Type type) {
		this.name = name;
		this.type = type;
	}
}
