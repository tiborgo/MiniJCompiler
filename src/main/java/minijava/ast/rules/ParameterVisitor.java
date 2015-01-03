package minijava.ast.rules;

public interface ParameterVisitor<A, T extends Throwable> {
	A visit(Parameter p) throws T;
}
