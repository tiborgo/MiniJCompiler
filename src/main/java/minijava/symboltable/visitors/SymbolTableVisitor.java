package minijava.symboltable.visitors;

import minijava.symboltable.tree.Program;

public interface SymbolTableVisitor<A, T extends Throwable> {

	public A visit(Program program) throws T;
}
