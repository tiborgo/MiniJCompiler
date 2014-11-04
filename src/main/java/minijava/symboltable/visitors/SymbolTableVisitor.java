package minijava.symboltable.visitors;

import minijava.symboltable.tree.Method;
import minijava.symboltable.tree.Program;
import minijava.symboltable.tree.Variable;
import minijava.symboltable.tree.Class;

public interface SymbolTableVisitor<A, T extends Throwable> {

	public A visit(Program program) throws T;
	public A visit(Class clazz) throws T;
	public A visit(Method method) throws T;
	public A visit(Variable variable) throws T;
}
