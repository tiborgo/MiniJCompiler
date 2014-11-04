package minijava.ast.visitors;

import minijava.ast.rules.Prg;

public interface PrgVisitor<A, T extends Throwable> {

	public A visit(Prg p) throws T;
}