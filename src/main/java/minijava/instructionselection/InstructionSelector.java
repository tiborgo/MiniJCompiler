package minijava.instructionselection;

import java.util.LinkedList;
import java.util.List;

import minijava.Configuration;
import minijava.Logger;
import minijava.instructionselection.assems.Assem;
import minijava.translate.layout.Fragment;
import minijava.translate.layout.FragmentProc;
import minijava.translate.tree.TreeStm;

public class InstructionSelector {

	public static List<Fragment<List<Assem>>> selectInstructions(
			Configuration config,
			List<FragmentProc<List<TreeStm>>> intermediateCanonicalized,
			MachineSpecifics machineSpecifics) throws InstructionSelectorException {

		try {
			List<Fragment<List<Assem>>> assemFragments = new LinkedList<>();
			for (FragmentProc<List<TreeStm>> fragment : intermediateCanonicalized) {
				assemFragments.add(machineSpecifics.codeGen(fragment));
			}

			String assembly = null;
			if (config.printPreAssembly) {
				assembly = machineSpecifics.printAssembly(assemFragments);
			}

			Logger.logVerbosely("Successfully generated assembly", assembly);

			return assemFragments;
		}
		catch (Exception e) {
			throw new InstructionSelectorException("Failed to generate assembly", e);
		}
	}
}
