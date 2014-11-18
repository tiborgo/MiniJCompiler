package minijava.intermediate.visitors;

import minijava.intermediate.tree.TreeExpCALL;
import minijava.intermediate.tree.TreeExpCONST;
import minijava.intermediate.tree.TreeExpESEQ;
import minijava.intermediate.tree.TreeExpMEM;
import minijava.intermediate.tree.TreeExpNAME;
import minijava.intermediate.tree.TreeExpOP;
import minijava.intermediate.tree.TreeExpTEMP;

public interface TreeExpVisitor<A, T extends Throwable> {

	public A visit(TreeExpCALL e) throws T;

	public A visit(TreeExpCONST e) throws T;

	public A visit(TreeExpESEQ e) throws T;

	public A visit(TreeExpMEM e) throws T;

	public A visit(TreeExpNAME e) throws T;

	public A visit(TreeExpOP e) throws T;

	public A visit(TreeExpTEMP e) throws T;
}
