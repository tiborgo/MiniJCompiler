package minijava.backend.i386;

import java.util.List;

import minijava.backend.Assem;
import minijava.backend.MachineSpecifics;
import minijava.backend.i386.AssemInstr.Kind;
import minijava.intermediate.Fragment;
import minijava.intermediate.FragmentProc;
import minijava.intermediate.Frame;
import minijava.intermediate.Label;
import minijava.intermediate.Temp;
import minijava.intermediate.tree.TreeStm;
import minijava.intermediate.visitors.AssemblerVisitor;

public class I386MachineSpecifics implements MachineSpecifics {

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
		return new I386Frame(name, paramCount);
	}

	@Override
	public List<Assem> spill(Frame frame, List<Assem> instrs, List<Temp> toSpill) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Fragment<List<Assem>> codeGen(Fragment<List<TreeStm>> frag) {
		AssemblerVisitor i386AssemblerVisitor = new AssemblerVisitor();
		FragmentProc<List<Assem>> assemFragement = frag.accept(i386AssemblerVisitor);
		return assemFragement;
	}

	@Override
	public String printAssembly(List<Fragment<List<Assem>>> frags) {

		StringBuilder stringBuilder = new StringBuilder();

		for (Fragment<List<Assem>> frag : frags) {
			// TODO: Treat FragmentProc as special case
			FragmentProc<List<Assem>> procedure = (FragmentProc<List<Assem>>) frag;
			// Safe caller-safe registers
			// FIXME: Use real ebp
			Operand ebp = new Operand.Reg(new Temp());
			// FIXME: Use real esp
			Operand esp = new Operand.Reg(new Temp());
			Assem saveFramePointer = new AssemUnaryOp(AssemUnaryOp.Kind.PUSH, ebp);
			Assem moveFramePointer = new AssemBinaryOp(AssemBinaryOp.Kind.MOV, ebp, esp);
			Assem moveStackPointer = new AssemBinaryOp(AssemBinaryOp.Kind.SUB, esp, new Operand.Imm(procedure.frame.size()));
			stringBuilder.append(saveFramePointer.accept(new I386PrintAssemblyVisitor())).append("\n");
			stringBuilder.append(moveFramePointer.accept(new I386PrintAssemblyVisitor())).append("\n");
			stringBuilder.append(moveStackPointer.accept(new I386PrintAssemblyVisitor())).append("\n");
			// Print body
			for (Assem assem : ((FragmentProc<List<Assem>>)frag).body) {
				stringBuilder.append(new I386PrintAssemblyVisitor().visit(assem)).append("\n");
			}
			// TODO: restore caller safe registers
		}
		return stringBuilder.toString();
	}
}
