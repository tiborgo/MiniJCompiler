package minijava.ast.visitors;

import minijava.ast.rules.Parameter;

public interface ParameterVisitor<A, T extends Throwable> {

	public A visit(Parameter p) throws T;
}