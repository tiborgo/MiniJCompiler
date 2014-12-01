package minijava.intermediate.visitors;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import minijava.backend.Assem;
import minijava.backend.i386.AssemBinaryOp;
import minijava.backend.i386.AssemBinaryOp.Kind;
import minijava.backend.i386.AssemJump;
import minijava.backend.i386.AssemLabel;
import minijava.backend.i386.AssemUnaryOp;
import minijava.backend.i386.Operand;
import minijava.intermediate.FragmentProc;
import minijava.intermediate.FragmentVisitor;
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

public class AssemblerVisitor implements
	FragmentVisitor<List<TreeStm>, FragmentProc<List<Assem>>>,
	TreeStmVisitor<List<Assem>, RuntimeException>,
 	TreeExpVisitor<Operand, RuntimeException> {

	private final List<Assem> instructions;

	public AssemblerVisitor() {
		this.instructions = new LinkedList<>();
	}

	@Override
	public FragmentProc<List<Assem>> visit(FragmentProc<List<TreeStm>> fragProc) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Operand visit(TreeExpCALL e) throws RuntimeException {
		// Push arguments on stack
		for (TreeExp arg : e.args) {
			// FIXME: Calculate correct address on stack
			Operand dst = new Operand.Mem(null, 1, null, 0);
			Operand src = arg.accept(this);
			instructions.add(new AssemBinaryOp(Kind.MOV, dst, src));
		}
		// TODO: Save Caller-Save registers?
		Operand result = e.func.accept(this);
		AssemJump callInstruction = new AssemJump(AssemJump.Kind.CALL, result);
		emit(callInstruction);
		return result;
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
		AssemUnaryOp.Kind operatorUnary = null;
		AssemBinaryOp.Kind operatorBinary = null;
		switch (e.op) {
			// Unary operators
			case MUL:
				operatorUnary = AssemUnaryOp.Kind.IMUL;
				break;
			case DIV:
				operatorUnary = AssemUnaryOp.Kind.IDIV;
				break;
			// Binary operators
			case PLUS:
				operatorBinary = AssemBinaryOp.Kind.ADD;
				break;
			case MINUS:
				operatorBinary = AssemBinaryOp.Kind.SUB;
				break;
			case AND:
				operatorBinary = AssemBinaryOp.Kind.AND;
				break;
			case OR:
				operatorBinary = AssemBinaryOp.Kind.OR;
				break;
			case LSHIFT:
				operatorBinary = AssemBinaryOp.Kind.SHL;
				break;
			case RSHIFT:
				operatorBinary = AssemBinaryOp.Kind.SHR;
				break;
			case ARSHIFT:
				operatorBinary = AssemBinaryOp.Kind.SAR;
				break;
			case XOR:
				operatorBinary = AssemBinaryOp.Kind.XOR;
				break;
			default:
				throw new UnsupportedOperationException(
						"Unsupported operator \""+ e.op + "\"");
		}

		// Unary instructions
		if (operatorUnary != AssemUnaryOp.Kind.IDIV) {
			// FIXME: Set destination to proper EAX register
			Operand eax = new Operand.Reg(null);
			AssemBinaryOp moveToEAX = new AssemBinaryOp(Kind.MOV, eax, o1);
			emit(moveToEAX);
			// TODO: Save register EDX?
			AssemUnaryOp division = new AssemUnaryOp(operatorUnary, o2);
			emit(division);
			return eax;
		} else if (operatorUnary != AssemUnaryOp.Kind.IMUL) {
			// FIXME: Set destination to proper EAX register
			Operand eax = new Operand.Reg(null);
			AssemBinaryOp moveToEAX = new AssemBinaryOp(Kind.MOV, eax, o1);
			emit(moveToEAX);
			// TODO: Save register EDX?
			AssemUnaryOp division = new AssemUnaryOp(operatorUnary, o2);
			emit(division);
			return eax;
		} else if (operatorUnary != null) {
			throw new UnsupportedOperationException("Unsupported operator \"" + operatorUnary + "\"");
		}

		// Binary instructions
		AssemBinaryOp binaryOperation = new AssemBinaryOp(operatorBinary, o1, o2);
		emit(binaryOperation);
		return o1;
	}

	@Override
	public Operand visit(TreeExpTEMP e) throws RuntimeException {
		return new Operand.Reg(e.temp);
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

	protected void emit(Assem instruction) {
		this.instructions.add(instruction);
	}
}
