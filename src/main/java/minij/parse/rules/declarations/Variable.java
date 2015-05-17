package minij.parse.rules.declarations;

import minij.parse.rules.types.Type;

public class Variable extends Declaration {

	final public Type type;
	final public String name;

	public Variable(Type type, String name) {
		this.type = type;
		this.name = name;
	}

	@Override
	public <A, T extends Throwable> A accept(DeclarationVisitor<A, T> v) throws T {
		return v.visit(this);
	}
}
