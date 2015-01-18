package minijava.backend.i386;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import minijava.backend.i386.assems.AssemBinaryOp;
import minijava.backend.i386.assems.AssemLabel;
import minijava.backend.i386.assems.Operand;
import minijava.backend.i386.assems.StackAllocation;
import minijava.backend.i386.assems.AssemBinaryOp.Kind;
import minijava.backend.i386.visitors.AssemblerVisitor;
import minijava.backend.i386.visitors.I386PrintAssemblyVisitor;
import minijava.instructionselection.MachineSpecifics;
import minijava.instructionselection.assems.Assem;
import minijava.translate.layout.Fragment;
import minijava.translate.layout.FragmentProc;
import minijava.translate.layout.Frame;
import minijava.translate.layout.Label;
import minijava.translate.layout.Temp;
import minijava.translate.layout.Frame.Location;
import minijava.translate.tree.TreeExp;
import minijava.translate.tree.TreeStm;
import minijava.util.Function;

public class I386MachineSpecifics implements MachineSpecifics {
	public static final Operand.Reg EAX = new Operand.Reg(new I386RegTemp("eax"));
	public static final Operand.Reg EBP = new Operand.Reg(new I386RegTemp("ebp"));
	public static final Operand.Reg ESP = new Operand.Reg(new I386RegTemp("esp"));
	public static final Operand.Reg EBX = new Operand.Reg(new I386RegTemp("ebx"));
	public static final Operand.Reg ECX = new Operand.Reg(new I386RegTemp("ecx"));
	public static final Operand.Reg EDX = new Operand.Reg(new I386RegTemp("edx"));
	public static final Operand.Reg ESI = new Operand.Reg(new I386RegTemp("esi"));
	public static final Operand.Reg EDI = new Operand.Reg(new I386RegTemp("edi"));

	public static final int WORD_SIZE = 4;

	private final String indentation = "\t";

	@Override
	public int getWordSize() {
		return WORD_SIZE;
	}

	@Override
	public Temp[] getAllRegisters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Temp[] getGeneralPurposeRegisters() {
		return new Temp[]{EAX.reg, EBP.reg, ESP.reg, EBX.reg, ECX.reg, EDX.reg, ESI.reg, EDI.reg};
	}

	@Override
	public Frame newFrame(Label name, int paramCount) {
		return new I386Frame(name, paramCount);
	}

	@Override
	public List<Assem> spill(Frame frame, List<Assem> instrs, List<Temp> toSpill) {
		
		Map<Temp, TreeExp> locals = new HashMap<>(toSpill.size());
		for (Temp t : toSpill) {
			locals.put(t, frame.addLocal(Location.IN_MEMORY));
		}
		
		List<Assem> spilledInstrs = new LinkedList<>();

		for (Assem instr : instrs) {
			
			boolean spilled = false;
		
			for (int i = 0; i < toSpill.size(); i++) {

				final Temp t = toSpill.get(i);

				List<Temp> use = instr.use();
				List<Temp> def = instr.def();

				if (use.contains(t) || def.contains(t)) {
					
					spilled = true;
					
					final Temp t_ = new Temp();
					
					TreeExp mExp = locals.get(t);
					AssemblerVisitor.StatementExpressionVisitor expVisitor = new AssemblerVisitor.StatementExpressionVisitor();
					Operand m = mExp.accept(expVisitor);
					spilledInstrs.addAll(expVisitor.getInstructions());

					if (use.contains(t)) {
						spilledInstrs.add(new AssemBinaryOp(Kind.MOV, new Operand.Reg(t_), m));
					}

					spilledInstrs.add(instr.rename(new Function<Temp, Temp>() {

						@Override
						public Temp apply(Temp a) {
							return a.equals(t) ? t_ : a;
						}

					}));

					if (def.contains(t)) {
						spilledInstrs.add(new AssemBinaryOp(Kind.MOV, m, new Operand.Reg(t_)));
					}
				}
			}
			
			if (!spilled) {
				/*
				 * Set amount of memory reserved on the stack according to
				 * the number of local variables. The local variable count
				 * is only available after spilling.
				 */
				if (instr instanceof StackAllocation) {
					
					int byteCount = frame.size() - I386MachineSpecifics.WORD_SIZE;
					// 4 (push ebp) + 4 (ret address) + byteCount
					int padding = 16 - ((byteCount + 8) % 16);
					Operand.Imm byteCountOperand = new Operand.Imm(byteCount + padding);
					((StackAllocation) instr).setByteCount(byteCountOperand);
				}
				spilledInstrs.add(instr);
			}
		}
		
		return spilledInstrs;
	}

	@Override
	public Fragment<List<Assem>> codeGen(Fragment<List<TreeStm>> frag) {
		AssemblerVisitor i386AssemblerVisitor = new AssemblerVisitor();
		return frag.accept(i386AssemblerVisitor);
	}

	@Override
	public String printAssembly(List<Fragment<List<Assem>>> frags) {

		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder
			.append("\t.intel_syntax noprefix" + System.lineSeparator())
			.append("\t.globl " + new Label("lmain").toString() + System.lineSeparator())
			.append(System.lineSeparator());

		for (Fragment<List<Assem>> frag : frags) {

			// TODO: Treat FragmentProc as special case
			FragmentProc<List<Assem>> procedure = (FragmentProc<List<Assem>>) frag;

			// Print instructions
			for (Assem assem : procedure.body) {
				if (!(assem instanceof AssemLabel)) {
					stringBuilder.append(indentation);
				}
				stringBuilder.append(assem.accept(new I386PrintAssemblyVisitor()));
				if (!(assem instanceof AssemLabel) || ((AssemLabel)assem).label.equals(procedure.frame.getName())) {
					stringBuilder.append(System.lineSeparator());
				}
			}
			stringBuilder.append(System.lineSeparator());
		}
		return stringBuilder.toString();
	}
}
