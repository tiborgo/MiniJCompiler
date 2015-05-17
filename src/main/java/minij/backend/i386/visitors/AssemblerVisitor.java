/*
 * MiniJCompiler: Compiler for a subset of the Java(R) language
 *
 * (C) Copyright 2014-2015 Tibor Goldschwendt <goldschwendt@cip.ifi.lmu.de>,
 * Michael Seifert <mseifert@error-reports.org>
 *
 * This file is part of MiniJCompiler.
 *
 * MiniJCompiler is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MiniJCompiler is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MiniJCompiler.  If not, see <http://www.gnu.org/licenses/>.
 */
package minij.backend.i386.visitors;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import minij.backend.i386.I386MachineSpecifics;
import minij.backend.i386.assems.AssemBinaryOp;
import minij.backend.i386.assems.AssemInstr;
import minij.backend.i386.assems.AssemJump;
import minij.backend.i386.assems.AssemLabel;
import minij.backend.i386.assems.AssemUnaryOp;
import minij.backend.i386.assems.Operand;
import minij.backend.i386.assems.OperandVisitor;
import minij.backend.i386.assems.StackAllocation;
import minij.backend.i386.assems.AssemBinaryOp.Kind;
import minij.backend.i386.assems.Operand.Mem;
import minij.instructionselection.assems.Assem;
import minij.translate.layout.FragmentProc;
import minij.translate.layout.FragmentVisitor;
import minij.translate.layout.Temp;
import minij.translate.tree.TreeExp;
import minij.translate.tree.TreeExpCALL;
import minij.translate.tree.TreeExpCONST;
import minij.translate.tree.TreeExpESEQ;
import minij.translate.tree.TreeExpMEM;
import minij.translate.tree.TreeExpNAME;
import minij.translate.tree.TreeExpOP;
import minij.translate.tree.TreeExpTEMP;
import minij.translate.tree.TreeExpVisitor;
import minij.translate.tree.TreeStm;
import minij.translate.tree.TreeStmCJUMP;
import minij.translate.tree.TreeStmEXP;
import minij.translate.tree.TreeStmJUMP;
import minij.translate.tree.TreeStmLABEL;
import minij.translate.tree.TreeStmMOVE;
import minij.translate.tree.TreeStmSEQ;
import minij.translate.tree.TreeStmVisitor;

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

		return new FragmentProc<List<Assem>>(fragProc.frame, instructions);
	}

	public static class StatementExpressionVisitor implements
	TreeStmVisitor<Void, RuntimeException>,
 	TreeExpVisitor<Operand, RuntimeException> {
		private final List<Assem> instructions;

		public StatementExpressionVisitor() {
			this.instructions = new LinkedList<>();
		}

		@Override
		public Operand visit(TreeExpCALL e) throws RuntimeException {

			// Push arguments on stack
			int parameterCount = e.args.size();
			int parameterSize = parameterCount*I386MachineSpecifics.WORD_SIZE;
			// TODO: Padding should be specific for OSX
			int padding = 16 - (parameterSize % 16);
			int stackIncrement = parameterSize + padding;
			emit(new AssemBinaryOp(Kind.SUB, I386MachineSpecifics.ESP, new Operand.Imm(stackIncrement)));
			for (int i = 0; i < e.args.size(); i++) {
				
				TreeExp arg = e.args.get(i);
				Operand src = arg.accept(this);
				// The source operand is a memory location, it needs to be loaded into a temporary first,
				// because i386 does not support a move operation between two memory locations.
				if (src instanceof Operand.Mem) {
					Operand temporary = new Operand.Reg(new Temp());
					emit(new AssemBinaryOp(Kind.MOV, temporary, src));
					src = temporary;
				}
				Operand dst = new Operand.Mem(I386MachineSpecifics.ESP.reg, null, null, I386MachineSpecifics.WORD_SIZE*i);
				emit(new AssemBinaryOp(Kind.MOV, dst, src));
			}

			Operand dest = e.func.accept(this);
			AssemJump callInstruction = new AssemJump(AssemJump.Kind.CALL, dest);
			emit(callInstruction);

			emit(new AssemBinaryOp(Kind.ADD, I386MachineSpecifics.ESP, new Operand.Imm(stackIncrement)));

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
			// Try to find a tile for the memory access subtree
			if (e.addr instanceof TreeExpOP) {
				TreeExpOP addressCalculation = (TreeExpOP) e.addr;
				
				if (addressCalculation.op == TreeExpOP.Op.PLUS ||
						addressCalculation.op == TreeExpOP.Op.MINUS) {
					
					TreeExp baseAddressExp = null;
					TreeExpCONST constExp = null;
					if (addressCalculation.left instanceof TreeExpCONST) {
						constExp = (TreeExpCONST) addressCalculation.left;
						baseAddressExp = addressCalculation.right;
					}
					else if (addressCalculation.right instanceof TreeExpCONST) {
						constExp = (TreeExpCONST) addressCalculation.right;
						baseAddressExp = addressCalculation.left;
					}
					
					int neg = (addressCalculation.op == TreeExpOP.Op.MINUS) ? -1 : 1;

					// Tile was found
					if (baseAddressExp != null && constExp != null) {
						Operand baseAddress = baseAddressExp.accept(this);
						if (baseAddress instanceof Operand.Reg) {
							return new Operand.Mem(((Operand.Reg) baseAddress).reg, null, null, constExp.value * neg);
						}
						else if (baseAddress instanceof Operand.Imm) {
							return new Operand.Mem(null, null, null, ((Operand.Imm) baseAddress).imm + constExp.value * neg);
						}
						else if (baseAddress instanceof Operand.Mem) {
							Operand.Mem memOperand = (Mem) baseAddress;
							return new Operand.Mem(memOperand.base, memOperand.scale, memOperand.index, memOperand.displacement + constExp.value * neg);
						}
					}
				}
			}

			// None of the above patterns match
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
					Temp t = new Temp();
					emit(new AssemBinaryOp(Kind.MOV, new Operand.Reg(t), operand));
					return new Operand.Mem(t);
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

				Operand.Reg savedEAX = new Operand.Reg(new Temp());
				Operand.Reg savedEDX = new Operand.Reg(new Temp());
				Operand.Reg result = new Operand.Reg(new Temp());
				Operand.Reg o2Temp = new Operand.Reg(new Temp());
				
				emit(
					// save eax
					new AssemBinaryOp(Kind.MOV, savedEAX, I386MachineSpecifics.EAX),
					// move left operand to eax
					new AssemBinaryOp(Kind.MOV, I386MachineSpecifics.EAX, o1),
					// store left operand in temp in case it is a mem or immediate
					new AssemBinaryOp(Kind.MOV, o2Temp, o2),
					// make backup of edx
					new AssemBinaryOp(Kind.MOV, savedEDX, I386MachineSpecifics.EDX),
					// idiv is actually a division of edx:eax by the operand.
					// So, when eax is negative (first bit is 1) all bits in edx must be 1
					// (so that first bit of quad edx:eax is 1 and value stays the same)
					// CDQ makes sure that edx is either all 1 when eax is negative
					// or 0 when eax is non-negative
					new AssemInstr(AssemInstr.Kind.CDQ),
					// actual operation
					new AssemUnaryOp(operatorUnary, o2Temp),
					// save the result
					new AssemBinaryOp(Kind.MOV, result, I386MachineSpecifics.EAX),
					// restore eax
					new AssemBinaryOp(Kind.MOV, I386MachineSpecifics.EAX, savedEAX),
					// restore edx
					new AssemBinaryOp(Kind.MOV, I386MachineSpecifics.EDX, savedEDX)
				);
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
			
			Operand tDst;
			
			// 1. dst must not be an immediate
			// 2. either dst or src must not be an mem
			if (dst instanceof Operand.Imm ||
					(dst instanceof Operand.Mem && src instanceof Operand.Mem)) {
				tDst = new Operand.Reg(new Temp());
				emit(new AssemBinaryOp(Kind.MOV, tDst, dst));
			}
			else {
				tDst = dst;
			}

			AssemBinaryOp assemBinaryOp = new AssemBinaryOp(
				Kind.MOV,
				tDst,
				src
			);
			emit(assemBinaryOp);
			
			// when both operands are mem locations we have to load the src into a temporary first
			// and then move into the dest as second step
			if (dst instanceof Operand.Mem && src instanceof Operand.Mem) {
				emit(new AssemBinaryOp(
					Kind.MOV,
					dst,
				    tDst
				));
			}
			
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
