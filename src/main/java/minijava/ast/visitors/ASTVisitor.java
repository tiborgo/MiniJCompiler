package minijava.ast.visitors;

import minijava.ast.rules.Prg;

public interface ASTVisitor<A, T extends Throwable> {

	public A visit(Prg p) throws T;
}