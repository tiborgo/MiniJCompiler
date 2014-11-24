package minijava.intermediate.visitors;

import java.util.Arrays;
import java.util.List;

import minijava.backend.Assem;
import minijava.backend.i386.AssemBinaryOp;
import minijava.backend.i386.AssemBinaryOp.Kind;
import minijava.backend.i386.AssemJump;
import minijava.backend.i386.AssemLabel;
import minijava.backend.i386.Operand;
import minijava.intermediate.FragmentProc;
import minijava.intermediate.FragmentVisitor;
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

public class AssemblerVisitor implements
	FragmentVisitor<List<TreeStm>, FragmentProc<List<Assem>>>,
	TreeStmVisitor<List<Assem>, RuntimeException>, 
 	TreeExpVisitor<Operand, RuntimeException> {

	
	@Override
	public FragmentProc<List<Assem>> visit(FragmentProc<List<TreeStm>> fragProc) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Operand visit(TreeExpCALL e) throws RuntimeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Operand visit(TreeExpCONST e) throws RuntimeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Operand visit(TreeExpESEQ e) throws RuntimeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Operand visit(TreeExpMEM e) throws RuntimeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Operand visit(TreeExpNAME e) throws RuntimeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Operand visit(TreeExpOP e) throws RuntimeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Operand visit(TreeExpTEMP e) throws RuntimeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Assem> visit(TreeStmMOVE stmMOVE) {
		
		AssemBinaryOp assemBinaryOp = new AssemBinaryOp(
			Kind.MOV,
			stmMOVE.dest.accept(this),
			stmMOVE.src.accept(this)
		);
		
		return Arrays.<Assem>asList(assemBinaryOp);
	}

	@Override
	public List<Assem> visit(TreeStmEXP stmEXP) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Assem> visit(TreeStmJUMP stmJUMP) {
		
		Operand dest = stmJUMP.dest.accept(this);
		AssemJump assemJump = new AssemJump(AssemJump.Kind.JMP, dest);
		return Arrays.<Assem>asList(assemJump);
	}

	@Override
	public List<Assem> visit(TreeStmCJUMP stmCJUMP) {
		
		AssemJump.Cond cond;
		
		switch(stmCJUMP.rel) {
		case EQ:
			cond = AssemJump.Cond.E;
			break;
		case NE:
			cond = AssemJump.Cond.NE;
			break;
		case LT:
			cond = AssemJump.Cond.L;
			break;
		case GT:
			cond = AssemJump.Cond.G;
			break;
		case LE:
			cond = AssemJump.Cond.LE;
			break;
		case GE:
			cond = AssemJump.Cond.GE;
			break;
		case ULT:
		case UGT:
		case ULE:
		case UGE:
		default:
			throw new UnsupportedOperationException("Unsigned conditions are not supported");
		}
		
		AssemBinaryOp cmpOp = new AssemBinaryOp(
			AssemBinaryOp.Kind.CMP,
			stmCJUMP.left.accept(this),
			stmCJUMP.right.accept(this)
		);
		AssemJump jump = new AssemJump(AssemJump.Kind.J, stmCJUMP.ltrue, cond);
		
		return Arrays.asList(cmpOp, jump);
	}

	@Override
	public List<Assem> visit(TreeStmSEQ stmSEQ) {
		throw new UnsupportedOperationException("Cannot translate TreeStmSEQ into assembler instructions");
	}

	@Override
	public List<Assem> visit(TreeStmLABEL stmLABEL) {
		AssemLabel label = new AssemLabel(stmLABEL.label);
		return Arrays.<Assem>asList(label);
	}

}
