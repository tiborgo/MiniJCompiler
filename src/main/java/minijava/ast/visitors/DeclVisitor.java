package minijava.ast.visitors;

import minijava.ast.rules.DeclClass;
import minijava.ast.rules.DeclMain;
import minijava.ast.rules.DeclMeth;
import minijava.ast.rules.DeclVar;

public interface DeclVisitor<A, T extends Throwable> {

	public A visit(DeclClass c) throws T;
	public A visit(DeclMain d)  throws T;
	public A visit(DeclMeth m)  throws T;
	public A visit(DeclVar d)   throws T;
}