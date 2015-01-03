package minijava.ast.rules.declarations;

public interface DeclVisitor<A, T extends Throwable> {
	A visit(DeclClass c) throws T;
	A visit(DeclMain d)  throws T;
	A visit(DeclMeth m)  throws T;
	A visit(DeclVar d)   throws T;
}
