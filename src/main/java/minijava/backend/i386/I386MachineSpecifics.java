package minijava.backend.i386;

import java.util.LinkedList;
import java.util.List;

import minijava.backend.Assem;
import minijava.backend.MachineSpecifics;
import minijava.intermediate.Fragment;
import minijava.intermediate.FragmentProc;
import minijava.intermediate.Frame;
import minijava.intermediate.Label;
import minijava.intermediate.Temp;
import minijava.intermediate.tree.TreeStm;

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
		return new I386Frame(name, paramCount, EAX.reg);
	}

	@Override
	public List<Assem> spill(Frame frame, List<Assem> instrs, List<Temp> toSpill) {
		// TODO Auto-generated method stub
		return null;
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
			.append("\t.intel_syntax" + System.lineSeparator())
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
