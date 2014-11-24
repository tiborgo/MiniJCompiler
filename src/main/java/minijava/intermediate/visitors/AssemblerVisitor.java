package minijava.intermediate.visitors;

import java.util.List;

import minijava.backend.Assem;
import minijava.backend.i386.AssemBinaryOp;
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
		return new Operand.Imm(e.value) ;
	}

	@Override
	public Operand visit(TreeExpESEQ e) throws RuntimeException {
		throw new RuntimeException("There must not be any ESEQ statements when " +
				"translating the intermediate representation into assembly.");
	}

	@Override
	public Operand visit(TreeExpMEM e) throws RuntimeException {
		return e.addr.accept(this);
	}

	@Override
	public Operand visit(TreeExpNAME e) throws RuntimeException {
		return new Operand.Label(e.label);
	}

	@Override
	public Operand visit(TreeExpOP e) throws RuntimeException {
		Operand o1 = e.left.accept(this);
		Operand o2 = e.right.accept(this);
		AssemBinaryOp.Kind operator = null;
		switch (e.op) {
			case PLUS:
				operator = AssemBinaryOp.Kind.ADD;
				break;
			case MINUS:
				operator = AssemBinaryOp.Kind.SUB;
				break;
			case MUL:
				// TODO: Multiplication uses the unary operation imul
			case DIV:
				// TODO: Multiplication uses the unary operation idiv
			case AND:
				operator = AssemBinaryOp.Kind.AND;
				break;
			case OR:
				operator = AssemBinaryOp.Kind.OR;
				break;
			case LSHIFT:
				operator = AssemBinaryOp.Kind.SHL;
				break;
			case RSHIFT:
				operator = AssemBinaryOp.Kind.SHR;
				break;
			case ARSHIFT:
				operator = AssemBinaryOp.Kind.SAR;
				break;
			case XOR:
				operator = AssemBinaryOp.Kind.XOR;
				break;
			default:
				throw new UnsupportedOperationException(
						"Unsupported operator \""+ e.op + "\"");
		}
		AssemBinaryOp binaryOperation = new AssemBinaryOp(operator, o1, o2);
		// TODO: Add binary operation to the list of assembler instructions
		return o1;
	}

	@Override
	public Operand visit(TreeExpTEMP e) throws RuntimeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Assem> visit(TreeStmMOVE stmMOVE) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Assem> visit(TreeStmEXP stmEXP) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Assem> visit(TreeStmJUMP stmJUMP) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Assem> visit(TreeStmCJUMP stmCJUMP) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Assem> visit(TreeStmSEQ stmSEQ) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Assem> visit(TreeStmLABEL stmLABEL) {
		// TODO Auto-generated method stub
		return null;
	}

}
