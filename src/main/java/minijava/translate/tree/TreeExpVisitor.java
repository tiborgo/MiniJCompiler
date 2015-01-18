package minijava.translate.tree;


public interface TreeExpVisitor<A, T extends Throwable> {

	public A visit(TreeExpCALL e) throws T;

	public A visit(TreeExpCONST e) throws T;

	public A visit(TreeExpESEQ e) throws T;

	public A visit(TreeExpMEM e) throws T;

	public A visit(TreeExpNAME e) throws T;

	public A visit(TreeExpOP e) throws T;

	public A visit(TreeExpTEMP e) throws T;
}
