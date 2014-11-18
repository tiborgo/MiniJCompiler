package minijava.intermediate.visitors;

import minijava.intermediate.tree.TreeExp;
import minijava.intermediate.tree.TreeExpCALL;
import minijava.intermediate.tree.TreeExpCONST;
import minijava.intermediate.tree.TreeExpESEQ;
import minijava.intermediate.tree.TreeExpMEM;
import minijava.intermediate.tree.TreeExpNAME;
import minijava.intermediate.tree.TreeExpOP;
import minijava.intermediate.tree.TreeExpTEMP;
import minijava.intermediate.tree.TreeStm;
import minijava.intermediate.tree.TreeStmCJUMP;
import minijava.intermediate.tree.TreeStmEXP;
import minijava.intermediate.tree.TreeStmJUMP;
import minijava.intermediate.tree.TreeStmLABEL;
import minijava.intermediate.tree.TreeStmMOVE;
import minijava.intermediate.tree.TreeStmSEQ;

public class CanonicalizeVisitor implements
	TreeExpVisitor<TreeExp, RuntimeException>,
	TreeStmVisitor<TreeStm, RuntimeException> {

	@Override
	public TreeStm visit(TreeStmMOVE stmMOVE) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TreeStm visit(TreeStmEXP stmEXP) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TreeStm visit(TreeStmJUMP stmJUMP) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TreeStm visit(TreeStmCJUMP stmCJUMP) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TreeStm visit(TreeStmSEQ stmSEQ) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TreeStm visit(TreeStmLABEL stmLABEL) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TreeExp visit(TreeExpCALL e) throws RuntimeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TreeExp visit(TreeExpCONST e) throws RuntimeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TreeExp visit(TreeExpESEQ e) throws RuntimeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TreeExp visit(TreeExpMEM e) throws RuntimeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TreeExp visit(TreeExpNAME e) throws RuntimeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TreeExp visit(TreeExpOP e) throws RuntimeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TreeExp visit(TreeExpTEMP e) throws RuntimeException {
		// TODO Auto-generated method stub
		return null;
	}

}
