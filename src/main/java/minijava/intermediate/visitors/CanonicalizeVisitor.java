package minijava.intermediate.visitors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import minijava.intermediate.Temp;
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
	TreeExpVisitor<CanonicalizeVisitor.CanonicalizedExp, RuntimeException>,
	TreeStmVisitor<List<TreeStm>, RuntimeException> {

	public static class CanonicalizedExp {
		public final List<TreeStm> stms;
		public final TreeExp exp;
		
		public CanonicalizedExp(List<TreeStm> stms, TreeExp exp) {
			this.stms = stms;
			this.exp = exp;
		}
		
		public CanonicalizedExp(TreeExp exp) {
			this.stms = Collections.emptyList();
			this.exp = exp;
		}
	}
	
	

	@Override
	public List<TreeStm> visit(TreeStmMOVE stmMOVE) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TreeStm> visit(TreeStmEXP stmEXP) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TreeStm> visit(TreeStmJUMP stmJUMP) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TreeStm> visit(TreeStmCJUMP stmCJUMP) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TreeStm> visit(TreeStmSEQ stmSEQ) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TreeStm> visit(TreeStmLABEL stmLABEL) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CanonicalizeVisitor.CanonicalizedExp visit(TreeExpCALL e) throws RuntimeException {
		
		CanonicalizedExp func = e.func.accept(this);
		
		List<TreeStm> stms = new ArrayList<>(func.stms);
		TreeExpTEMP funcTemp = new TreeExpTEMP(new Temp());
		stms.add(new TreeStmMOVE(funcTemp, func.exp));
		
		List<TreeExp> argTemps = new LinkedList<>();
		
		for (TreeExp arg : e.args) {
			CanonicalizedExp canonicalizedArg = arg.accept(this);
			stms.addAll(canonicalizedArg.stms);
			TreeExpTEMP argTemp = new TreeExpTEMP(new Temp());
			stms.add(new TreeStmMOVE(argTemp, canonicalizedArg.exp));
			argTemps.add(argTemp);
		}
		
		TreeExpTEMP returnTemp = new TreeExpTEMP(new Temp());
		
		stms.add(new TreeStmMOVE(
			returnTemp,
			new TreeExpCALL(funcTemp, argTemps))
		);
		
		return new CanonicalizedExp(stms, returnTemp);
	}

	@Override
	public CanonicalizeVisitor.CanonicalizedExp visit(TreeExpCONST e) throws RuntimeException {
		return new CanonicalizedExp(e);
	}

	@Override
	public CanonicalizeVisitor.CanonicalizedExp visit(TreeExpESEQ e) throws RuntimeException {
		
		List<TreeStm> stms = e.stm.accept(this);
		CanonicalizedExp exp = e.res.accept(this);
		
		stms.addAll(exp.stms);
		
		return new CanonicalizedExp(stms, exp.exp);
	}

	@Override
	public CanonicalizeVisitor.CanonicalizedExp visit(TreeExpMEM e) throws RuntimeException {
		
		CanonicalizedExp exp = e.addr.accept(this);
		
		return new CanonicalizedExp(
			exp.stms,
			new TreeExpMEM(exp.exp)
		);
	}

	@Override
	public CanonicalizeVisitor.CanonicalizedExp visit(TreeExpNAME e) throws RuntimeException {
		return new CanonicalizedExp(e);
	}

	@Override
	public CanonicalizeVisitor.CanonicalizedExp visit(TreeExpOP e) throws RuntimeException {
		
		CanonicalizedExp left = e.left.accept(this);
		CanonicalizedExp right = e.right.accept(this);
		
		List<TreeStm> stms = new ArrayList<>(left.stms.size() + right.stms.size());
		stms.addAll(left.stms);
		stms.addAll(right.stms);
		
		return new CanonicalizedExp(
			stms,
			new TreeExpOP(e.op, left.exp, right.exp)
		);
	}

	@Override
	public CanonicalizeVisitor.CanonicalizedExp visit(TreeExpTEMP e) throws RuntimeException {
		return new CanonicalizedExp(e);
	}

}
