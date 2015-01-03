package minijava.symboltable.visitors;

import minijava.symboltable.tree.Class;
import minijava.symboltable.tree.Program;

public interface SymbolTableVisitor<A, T extends Throwable> {

	public A visit(Program program) throws T;
	public A visit(Class clazz) throws T;
}
