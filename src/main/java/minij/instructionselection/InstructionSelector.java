package minij.instructionselection;

import java.util.LinkedList;
import java.util.List;

import minij.Configuration;
import minij.Logger;
import minij.instructionselection.assems.Assem;
import minij.translate.layout.Fragment;
import minij.translate.layout.FragmentProc;
import minij.translate.tree.TreeStm;

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

			Logger.logVerbosely("Successfully generated assembly");
			
			if (config.printPreAssembly) {
				Logger.log(machineSpecifics.printAssembly(assemFragments));
			}

			return assemFragments;
		}
		catch (Exception e) {
			throw new InstructionSelectorException("Failed to generate assembly", e);
		}
	}
}
