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
import minijava.intermediate.visitors.AssemblerVisitor;

public class I386MachineSpecifics implements MachineSpecifics {
	private final Operand.Reg eax;
	private final Operand.Reg ebp;
	private final Operand.Reg esp;
	private final String indentation = "\t";

	public I386MachineSpecifics() {
		eax = new Operand.Reg(new I386RegTemp("eax"));
		ebp = new Operand.Reg(new I386RegTemp("ebp"));
		esp = new Operand.Reg(new I386RegTemp("esp"));
	}

	@Override
	public int getWordSize() {
		return 4;
	}

	@Override
	public Temp[] getAllRegisters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Temp[] getGeneralPurposeRegisters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Frame newFrame(Label name, int paramCount) {
		return new I386Frame(name, paramCount, eax.reg);
	}

	@Override
	public List<Assem> spill(Frame frame, List<Assem> instrs, List<Temp> toSpill) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Fragment<List<Assem>> codeGen(Fragment<List<TreeStm>> frag) {
		AssemblerVisitor i386AssemblerVisitor = new AssemblerVisitor(eax, ebp, esp);
		FragmentProc<List<Assem>> assemFragement = frag.accept(i386AssemblerVisitor);
		return assemFragement;
	}

	@Override
	public String printAssembly(List<Fragment<List<Assem>>> frags) {

		StringBuilder stringBuilder = new StringBuilder();

		for (Fragment<List<Assem>> frag : frags) {
			// TODO: Treat FragmentProc as special case
			FragmentProc<List<Assem>> procedure = (FragmentProc<List<Assem>>) frag;
			List<Assem> procedureWithEntryExitCode = new LinkedList<>();

			Assem functionLabel = new AssemLabel(procedure.frame.getName());
			procedureWithEntryExitCode.add(functionLabel);

			// Prologue
			Assem saveFramePointer = new AssemUnaryOp(AssemUnaryOp.Kind.PUSH, ebp);
			procedureWithEntryExitCode.add(saveFramePointer);
			Assem moveFramePointer = new AssemBinaryOp(AssemBinaryOp.Kind.MOV, ebp, esp);
			procedureWithEntryExitCode.add(moveFramePointer);
			// TODO: Allocate space on stack for local variables
			int localVariableSize = 0;
			Assem moveStackPointer = new AssemBinaryOp(AssemBinaryOp.Kind.SUB, esp, new Operand.Imm(localVariableSize));
			procedureWithEntryExitCode.add(moveStackPointer);

			// TODO: Save callee-safe registers

			procedureWithEntryExitCode.addAll(procedure.body);

			// Restore caller-safe registers
			Assem leave = new AssemInstr(AssemInstr.Kind.LEAVE);
			procedureWithEntryExitCode.add(leave);

			Assem ret = new AssemInstr(AssemInstr.Kind.RET);
			procedureWithEntryExitCode.add(ret);

			// Print instructions
			for (Assem assem : procedureWithEntryExitCode) {
				if (!(assem instanceof AssemLabel)) {
					stringBuilder.append(indentation);
				}
				stringBuilder.append(assem.accept(new I386PrintAssemblyVisitor())).append("\n");
			}
		}
		return stringBuilder.toString();
	}
}
