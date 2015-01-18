package minijava.translate.visitors;

import minijava.translate.tree.TreeExpCALL;
import minijava.translate.tree.TreeExpCONST;
import minijava.translate.tree.TreeExpESEQ;
import minijava.translate.tree.TreeExpMEM;
import minijava.translate.tree.TreeExpNAME;
import minijava.translate.tree.TreeExpOP;
import minijava.translate.tree.TreeExpTEMP;

public interface TreeExpVisitor<A, T extends Throwable> {

	public A visit(TreeExpCALL e) throws T;

	public A visit(TreeExpCONST e) throws T;

	public A visit(TreeExpESEQ e) throws T;

	public A visit(TreeExpMEM e) throws T;

	public A visit(TreeExpNAME e) throws T;

	public A visit(TreeExpOP e) throws T;

	public A visit(TreeExpTEMP e) throws T;
}
