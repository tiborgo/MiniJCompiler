package minijava.backend.i386;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import minijava.backend.Assem;
import minijava.backend.i386.AssemBinaryOp.Kind;
import minijava.backend.i386.Operand.Mem;
import minijava.backend.i386.visitors.OperandVisitor;
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
		instructions.add(new AssemUnaryOp(AssemUnaryOp.Kind.PUSH, I386MachineSpecifics.EBP));
		instructions.add(new AssemBinaryOp(AssemBinaryOp.Kind.MOV, I386MachineSpecifics.EBP, I386MachineSpecifics.ESP));
		instructions.add(new StackAllocation(new Operand.Imm(0)));

		// save callee-save registers: ebx, esi, edi, ebp (ebp already saved by prologue)
		Operand.Reg ebxTemp = new Operand.Reg(new Temp());
		Operand.Reg esiTemp = new Operand.Reg(new Temp());
		Operand.Reg ediTemp = new Operand.Reg(new Temp());
		instructions.add(new AssemBinaryOp(AssemBinaryOp.Kind.MOV, ebxTemp, I386MachineSpecifics.EBX));
		instructions.add(new AssemBinaryOp(AssemBinaryOp.Kind.MOV, esiTemp, I386MachineSpecifics.ESI));
		instructions.add(new AssemBinaryOp(AssemBinaryOp.Kind.MOV, ediTemp, I386MachineSpecifics.EDI));
		
		// Make eax available to the register allocator
		instructions.add(new AssemBinaryOp(AssemBinaryOp.Kind.MOV, I386MachineSpecifics.EAX, new Operand.Imm(0)));

		// Load parameters from stack into temp
		for (int i = 0; i < fragProc.frame.getParameterCount(); i++) {
			StatementExpressionVisitor visitor = new StatementExpressionVisitor();
			Operand param = fragProc.frame.getParameter(i).accept(visitor);
			instructions.addAll(visitor.getInstructions());
			Operand address = new Operand.Mem(I386MachineSpecifics.EBP.reg, null, null, (i+2) * I386MachineSpecifics.WORD_SIZE);
			instructions.add(new AssemBinaryOp(AssemBinaryOp.Kind.MOV, param, address));
		}
		
		// translate body
		for (TreeStm statement : fragProc.body) {
			StatementExpressionVisitor visitor = new StatementExpressionVisitor();
			statement.accept(visitor);
			instructions.addAll(visitor.getInstructions());
		}

		// restore callee-save registers: ebx, esi, edi, ebp (ebp will be restored by leave)
		instructions.add(new AssemBinaryOp(AssemBinaryOp.Kind.MOV, I386MachineSpecifics.EBX, ebxTemp));
		instructions.add(new AssemBinaryOp(AssemBinaryOp.Kind.MOV, I386MachineSpecifics.ESI, esiTemp));
		instructions.add(new AssemBinaryOp(AssemBinaryOp.Kind.MOV, I386MachineSpecifics.EDI, ediTemp));

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
				Operand src = arg.accept(this);
				// The source operand is a memory location, it needs to be loaded into a temporary first,
				// because i386 does not support a move operation between two memory locations.
				if (src instanceof Operand.Mem) {
					Operand temporary = new Operand.Reg(new Temp());
					emit(new AssemBinaryOp(Kind.MOV, temporary, src));
					src = temporary;
				}
				Operand dst = new Operand.Mem(I386MachineSpecifics.ESP.reg, null, null, I386MachineSpecifics.WORD_SIZE*e.args.indexOf(arg));
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
				public Mem visit(Operand.Imm operand) {
					return new Operand.Mem(null, null, null, operand.imm);
				}

				@Override
				public Mem visit(Operand.Label operand) {
					throw new UnsupportedOperationException("Cannot convert label to address");
				}

				@Override
				public Mem visit(Operand.Mem operand) {
					return operand;
				}

				@Override
				public Mem visit(Operand.Reg operand) {
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

			assert(!(o1 instanceof Operand.Label));

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

				// TODO: should we save %edx?

				Operand.Reg savedEAX = new Operand.Reg(new Temp());
				//Operand.Reg savedEDX = new Operand.Reg(new Temp());
				Operand.Reg o2Temp = new Operand.Reg(new Temp());
				Operand.Reg result = new Operand.Reg(new Temp());

				AssemBinaryOp saveEAX = new AssemBinaryOp(Kind.MOV, savedEAX, I386MachineSpecifics.EAX);
				//AssemBinaryOp saveEDX = new AssemBinaryOp(Kind.MOV, savedEDX, I386MachineSpecifics.EDX);
				AssemBinaryOp moveToEAX = new AssemBinaryOp(Kind.MOV, I386MachineSpecifics.EAX, o1);
				AssemBinaryOp moveToO2Temp = new AssemBinaryOp(Kind.MOV, o2Temp, o2);
				AssemUnaryOp division = new AssemUnaryOp(operatorUnary, o2Temp);
				AssemBinaryOp saveResult = new AssemBinaryOp(Kind.MOV, result, I386MachineSpecifics.EAX);
				AssemBinaryOp restoreEAX = new AssemBinaryOp(Kind.MOV, I386MachineSpecifics.EAX, savedEAX);
				//AssemBinaryOp restoreEDX = new AssemBinaryOp(Kind.MOV, I386MachineSpecifics.EDX, savedEDX);
				emit(saveEAX/*, saveEDX*/, moveToEAX, moveToO2Temp, division, saveResult, restoreEAX/*, restoreEDX*/);
				return result;
			}
			else {
				// Binary instructions
				
				// Destination of most arithmetical and some logical operations cannot be immediates
				// (whenever the destination is changed by the operation it must be a register or memory location)
				
				Operand.Reg o1_ = new Operand.Reg(new Temp());
				emit(new AssemBinaryOp(Kind.MOV, o1_, o1));
				
				AssemBinaryOp binaryOperation = new AssemBinaryOp(operatorBinary, o1_, o2);
				emit(binaryOperation);
				return o1_;
			}
		}

		@Override
		public Operand visit(TreeExpTEMP e) throws RuntimeException {
			return new Operand.Reg(e.temp);
		}

		@Override
		public Void visit(TreeStmMOVE stmMOVE) {
			
			Operand dst = stmMOVE.dest.accept(this);
			Operand src = stmMOVE.src.accept(this);
			
			// 1. dst must not be an immediate
			// TODO: maybe revert jump?
			// 2. either dst or src must not be an mem
			if (dst instanceof Operand.Imm ||
					(dst instanceof Operand.Mem && src instanceof Operand.Mem)) {
				Operand.Reg tDst = new Operand.Reg(new Temp());
				emit(new AssemBinaryOp(Kind.MOV, tDst, dst));
				dst = tDst;
			}

			AssemBinaryOp assemBinaryOp = new AssemBinaryOp(
				Kind.MOV,
				dst,
				src
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

			Operand left  = stmCJUMP.left.accept(this);
			Operand right = stmCJUMP.right.accept(this);

			// 1. dst must not be an immediate
			// TODO: maybe revert jump?
			// 2. either dst or src must not be an mem
			if (left instanceof Operand.Imm ||
					(left instanceof Operand.Mem && right instanceof Operand.Mem)) {
				Operand.Reg tLeft = new Operand.Reg(new Temp());
				emit(new AssemBinaryOp(Kind.MOV, tLeft, left));
				left = tLeft;
			}

			AssemBinaryOp cmpOp = new AssemBinaryOp(
				AssemBinaryOp.Kind.CMP,
				left,
				right
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
