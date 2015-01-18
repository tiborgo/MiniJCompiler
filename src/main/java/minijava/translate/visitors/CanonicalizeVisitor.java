package minijava.translate.visitors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import minijava.translate.Temp;
import minijava.translate.tree.TreeExp;
import minijava.translate.tree.TreeExpCALL;
import minijava.translate.tree.TreeExpCONST;
import minijava.translate.tree.TreeExpESEQ;
import minijava.translate.tree.TreeExpMEM;
import minijava.translate.tree.TreeExpNAME;
import minijava.translate.tree.TreeExpOP;
import minijava.translate.tree.TreeExpTEMP;
import minijava.translate.tree.TreeStm;
import minijava.translate.tree.TreeStmCJUMP;
import minijava.translate.tree.TreeStmEXP;
import minijava.translate.tree.TreeStmJUMP;
import minijava.translate.tree.TreeStmLABEL;
import minijava.translate.tree.TreeStmMOVE;
import minijava.translate.tree.TreeStmSEQ;

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
