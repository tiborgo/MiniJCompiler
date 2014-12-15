package minijava.intermediate.visitors;

import java.util.Collections;
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
import minijava.intermediate.Label;
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
	FragmentVisitor<List<TreeStm>, FragmentProc<List<Assem>>> {
	private final Operand.Reg eax;
	private final Operand.Reg ebp;
	private final Operand.Reg esp;

	public AssemblerVisitor(Operand.Reg eax, Operand.Reg ebp, Operand.Reg esp) {
		this.eax = eax;
		this.ebp = ebp;
		this.esp = esp;
	}

	@Override
	public FragmentProc<List<Assem>> visit(FragmentProc<List<TreeStm>> fragProc) {
		List<Assem> instructions = new LinkedList<>();
		for (TreeStm statement : fragProc.body) {
			StatementExpressionVisitor visitor = new StatementExpressionVisitor(eax, ebp, esp);
			statement.accept(visitor);
			instructions.addAll(visitor.getInstructions());
		}
		// FIXME: Set correct frame?
		return new FragmentProc<List<Assem>>(fragProc.frame, instructions);
	}

	protected static class StatementExpressionVisitor implements
	TreeStmVisitor<Void, RuntimeException>,
 	TreeExpVisitor<Operand, RuntimeException> {
		private final List<Assem> instructions;
		private final Operand.Reg eax;
		private final Operand.Reg ebp;
		private final Operand.Reg esp;

		public StatementExpressionVisitor(Operand.Reg eax, Operand.Reg ebp, Operand.Reg esp) {
			this.instructions = new LinkedList<>();
			this.eax = eax;
			this.ebp = ebp;
			this.esp = esp;
		}

		@Override
		public Operand visit(TreeExpCALL e) throws RuntimeException {
			// TODO: Save Caller-Save registers?

			// Push arguments on stack
			int parameterCount = e.args.size();
			// TODO: Use machine specifics to get word size
			int parameterSize = parameterCount*4;
			// TODO: Padding should be specific for OSX
			int padding = 16 - (parameterSize % 16);
			int stackIncrement = parameterSize + padding;
			emit(new AssemBinaryOp(Kind.SUB, esp, new Operand.Imm(stackIncrement)));
			for (TreeExp arg : e.args) {
				// TODO: Calculate address according to word size
				Operand dst = new Operand.Mem(esp.reg, null, null, 4*e.args.indexOf(arg));
				Operand src = arg.accept(this);
				emit(new AssemBinaryOp(Kind.MOV, dst, src));
			}

			Operand dest = e.func.accept(this);
			AssemJump callInstruction = new AssemJump(AssemJump.Kind.CALL, dest);
			emit(callInstruction);

			emit(new AssemBinaryOp(Kind.ADD, esp, new Operand.Imm(stackIncrement)));

			// TODO: Restore Caller-Save registers?

			return dest;
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
			if (operatorUnary != null) {
				if (operatorUnary == AssemUnaryOp.Kind.IDIV) {
					AssemBinaryOp moveToEAX = new AssemBinaryOp(Kind.MOV, eax, o1);
					// TODO: Save register EDX?
					AssemUnaryOp division = new AssemUnaryOp(operatorUnary, o2);
					emit(moveToEAX, division);
					return eax;
				} else if (operatorUnary == AssemUnaryOp.Kind.IMUL) {
					AssemBinaryOp moveToEAX = new AssemBinaryOp(Kind.MOV, eax, o1);
					// TODO: Save register EDX?
					AssemUnaryOp division = new AssemUnaryOp(operatorUnary, o2);
					emit(moveToEAX, division);
					return eax;
				} else {
					throw new UnsupportedOperationException("Unsupported operator \"" + operatorUnary + "\"");
				}
			} else {
				// Binary instructions
				AssemBinaryOp binaryOperation = new AssemBinaryOp(operatorBinary, o1, o2);
				emit(binaryOperation);
				return o1;
			}
		}

		@Override
		public Operand visit(TreeExpTEMP e) throws RuntimeException {
			return new Operand.Reg(e.temp);
		}

		@Override
		public Void visit(TreeStmMOVE stmMOVE) {

			AssemBinaryOp assemBinaryOp = new AssemBinaryOp(
				Kind.MOV,
				stmMOVE.dest.accept(this),
				stmMOVE.src.accept(this)
			);
			emit(assemBinaryOp);
			return null;
		}

		@Override
		public Void visit(TreeStmEXP stmEXP) {
			stmEXP.exp.accept(this);
			return null;
		}

		@Override
		public Void visit(TreeStmJUMP stmJUMP) {

			Operand dest = stmJUMP.dest.accept(this);
			AssemJump assemJump = new AssemJump(AssemJump.Kind.JMP, dest);
			emit(assemJump);
			return null;
		}

		@Override
		public Void visit(TreeStmCJUMP stmCJUMP) {

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
			AssemJump jump = new AssemJump(AssemJump.Kind.J, new Operand.Label(stmCJUMP.ltrue), cond);
			emit(cmpOp, jump);
			return null;
		}

		@Override
		public Void visit(TreeStmSEQ stmSEQ) {
			throw new UnsupportedOperationException("Cannot translate TreeStmSEQ into assembler instructions");
		}

		@Override
		public Void visit(TreeStmLABEL stmLABEL) {
			AssemLabel label = new AssemLabel(stmLABEL.label);
			emit(label);
			return null;
		}

		protected void emit(Assem... instructions) {
			for (Assem instruction : instructions) {
				this.instructions.add(instruction);
			}
		}

		public List<Assem> getInstructions() {
			return Collections.unmodifiableList(instructions);
		}
	}
}
