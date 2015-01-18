package minijava.parse.rules.declarations;

public interface DeclarationVisitor<A, T extends Throwable> {
	A visit(Class c) throws T;

	A visit(Main d) throws T;

	A visit(Method m) throws T;

	A visit(Variable d) throws T;
}
