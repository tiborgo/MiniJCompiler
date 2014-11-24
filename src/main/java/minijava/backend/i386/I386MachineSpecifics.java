package minijava.backend.i386;

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
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return null;
	}

}
