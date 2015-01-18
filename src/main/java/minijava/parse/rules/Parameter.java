package minijava.parse.rules;

import minijava.parse.rules.types.Type;

public class Parameter {

	final public String id;
	final public Type type;

	public Parameter(String id, Type type) {
		this.id = id;
		this.type = type;
	}

	public <A, T extends Throwable> A accept(ParameterVisitor<A, T> v) throws T {
		return v.visit(this);
	}

}
