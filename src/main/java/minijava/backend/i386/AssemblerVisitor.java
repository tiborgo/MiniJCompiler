package minijava.backend.i386;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import minijava.backend.Assem;
import minijava.backend.Instruction;
import minijava.backend.i386.instructions.Idiv;
import minijava.backend.i386.instructions.Imul;
import minijava.backend.i386.instructions.Push;
import minijava.backend.i386.visitors.OperandVisitor;
import minijava.backend.i386.AssemBinaryOp.Kind;
import minijava.backend.i386.Operand.Imm;
import minijava.backend.i386.Operand.Label;
import minijava.backend.i386.Operand.Mem;
import minijava.backend.i386.Operand.Reg;
import minijava.intermediate.FragmentProc;
import minijava.intermediate.FragmentVisitor;
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
import minijava.intermediate.visitors.TreeExpVisitor;
import minijava.intermediate.visitors.TreeStmVisitor;

public class AssemblerVisitor implements
	FragmentVisitor<List<TreeStm>, FragmentProc<List<Assem>>> {

	public AssemblerVisitor() {
	}

	@Override
	public FragmentProc<List<Assem>> visit(FragmentProc<List<TreeStm>> fragProc) {
		List<Assem> instructions = new LinkedList<>();
		
		// Function label
		instructions.add(new AssemLabel(fragProc.frame.getName()));
		
		// Prologue
		instructions.add(new Push(I386MachineSpecifics.EBP));
		instructions.add(new AssemBinaryOp(AssemBinaryOp.Kind.MOV, I386MachineSpecifics.EBP, I386MachineSpecifics.ESP));

		// TODO: Allocate space on stack for local variables
		int localVariableSize = 0;
		// 4 (push ebp) + 4 (ret address) + localVariableSize
		int padding = 16 - ((localVariableSize + 8) % 16);
		instructions.add(new AssemBinaryOp(AssemBinaryOp.Kind.SUB, I386MachineSpecifics.ESP, new Operand.Imm(localVariableSize + padding)));
		
		for (TreeStm statement : fragProc.body) {
			StatementExpressionVisitor visitor = new StatementExpressionVisitor();
			statement.accept(visitor);
			instructions.addAll(visitor.getInstructions());
		}
		
		// Epilogue
		Assem leave = new AssemInstr(AssemInstr.Kind.LEAVE);
		instructions.add(leave);

		Assem ret = new AssemInstr(AssemInstr.Kind.RET);
		instructions.add(ret);
		
		// FIXME: Set correct frame?
		return new FragmentProc<List<Assem>>(fragProc.frame, instructions);
	}

	protected static class StatementExpressionVisitor implements
	TreeStmVisitor<Void, RuntimeException>,
 	TreeExpVisitor<Operand, RuntimeException> {
		private final List<Assem> instructions;

		public StatementExpressionVisitor() {
			this.instructions = new LinkedList<>();
		}

		@Override
		public Operand visit(TreeExpCALL e) throws RuntimeException {
			// TODO: Save Caller-Save registers?

			// Push arguments on stack
			int parameterCount = e.args.size();
			int parameterSize = parameterCount*I386MachineSpecifics.WORD_SIZE;
			// TODO: Padding should be specific for OSX
			int padding = 16 - (parameterSize % 16);
			int stackIncrement = parameterSize + padding;
			emit(new AssemBinaryOp(Kind.SUB, I386MachineSpecifics.ESP, new Operand.Imm(stackIncrement)));
			for (TreeExp arg : e.args) {
				Operand dst = new Operand.Mem(I386MachineSpecifics.ESP.reg, null, null, I386MachineSpecifics.WORD_SIZE*e.args.indexOf(arg));
				Operand src = arg.accept(this);
				emit(new AssemBinaryOp(Kind.MOV, dst, src));
			}

			Operand dest = e.func.accept(this);
			AssemJump callInstruction = new AssemJump(AssemJump.Kind.CALL, dest);
			emit(callInstruction);

			emit(new AssemBinaryOp(Kind.ADD, I386MachineSpecifics.ESP, new Operand.Imm(stackIncrement)));

			// TODO: Restore Caller-Save registers?

			return I386MachineSpecifics.EAX;
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
			Operand address = e.addr.accept(this);
			
			OperandVisitor<Operand.Mem, RuntimeException> memVisitor = new OperandVisitor<Operand.Mem, RuntimeException>() {

				@Override
				public Mem visit(Imm operand) {
					return new Operand.Mem(null, null, null, operand.imm);
				}

				@Override
				public Mem visit(Label operand) {
					throw new UnsupportedOperationException("Cannot convert label to address");
				}

				@Override
				public Mem visit(Mem operand) {
					return operand;
				}

				@Override
				public Mem visit(Reg operand) {
					return new Operand.Mem(operand.reg);
				}
			};
			
			return address.accept(memVisitor);
		}

		@Override
		public Operand visit(TreeExpNAME e) throws RuntimeException {
			return new Operand.Label(e.label);
		}

		@Override
		public Operand visit(TreeExpOP e) throws RuntimeException {
			Operand o1 = e.left.accept(this);
			Operand o2 = e.right.accept(this);
			AssemBinaryOp.Kind operatorBinary = null;
			switch (e.op) {
				// Unary operators
				case MUL:
					AssemBinaryOp moveToEAX = new AssemBinaryOp(Kind.MOV, I386MachineSpecifics.EAX, o1);
					// TODO: Save register EDX?
					Operand.Reg o2Temp = new Operand.Reg(new Temp());
					AssemBinaryOp moveToO2Temp = new AssemBinaryOp(Kind.MOV, o2Temp, o2);
					Instruction multiplication = new Imul(o2Temp);
					emit(moveToEAX, moveToO2Temp, multiplication);
					return I386MachineSpecifics.EAX;
				case DIV:
					AssemBinaryOp moveToEAX_div = new AssemBinaryOp(Kind.MOV, I386MachineSpecifics.EAX, o1);
					// TODO: Save register EDX?
					Operand.Reg o2Temp_div = new Operand.Reg(new Temp());
					AssemBinaryOp moveToO2Temp_div = new AssemBinaryOp(Kind.MOV, o2Temp_div, o2);
					Instruction division = new Idiv(o2Temp_div);
					emit(moveToEAX_div, moveToO2Temp_div, division);
					return I386MachineSpecifics.EAX;
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

			// Binary instructions
			if (o1 instanceof Operand.Imm) {
				Operand.Reg o1_ = new Operand.Reg(new Temp());
				emit(new AssemBinaryOp(Kind.MOV, o1_, o1));
				o1 = o1_;
			}
			AssemBinaryOp binaryOperation = new AssemBinaryOp(operatorBinary, o1, o2);
			emit(binaryOperation);
			return o1;
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
