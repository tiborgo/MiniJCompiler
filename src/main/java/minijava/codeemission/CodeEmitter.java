package minijava.codeemission;

import java.util.List;

import minijava.Configuration;
import minijava.Logger;
import minijava.instructionselection.MachineSpecifics;
import minijava.instructionselection.assems.Assem;
import minijava.translate.layout.Fragment;

public class CodeEmitter {

	public static String emitCode(Configuration config, List<Fragment<List<Assem>>> assemFragments, MachineSpecifics machineSpecifics) throws CodeEmitterException {
		
		try {
			String assembly = machineSpecifics.printAssembly(assemFragments);;
			
			Logger.logVerbosely("Successfully generated assembly", (config.printAssembly) ? assembly : null);
			
			return assembly;
		}
		catch (Exception e) {
			throw new CodeEmitterException("Failed to generate assembly", e);
		}
	}
}
