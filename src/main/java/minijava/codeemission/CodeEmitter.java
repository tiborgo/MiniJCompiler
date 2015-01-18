package minijava.codeemission;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;

import minijava.Configuration;
import minijava.Logger;
import minijava.MiniJavaCompiler;
import minijava.instructionselection.MachineSpecifics;
import minijava.instructionselection.assems.Assem;
import minijava.translate.layout.Fragment;

public class CodeEmitter {

	public static String emitCode(Configuration config, List<Fragment<List<Assem>>> assemFragments, MachineSpecifics machineSpecifics) throws CodeEmitterException {
		
		try {
			String assembly = machineSpecifics.printAssembly(assemFragments);;
			
			if (config.codeEmission) {
				PrintWriter out = new PrintWriter(MiniJavaCompiler.RUNTIME_DIRECTORY.toString() + File.separator + config.outputFile);
				out.print(assembly);
				out.close();
			}
			
			Logger.logVerbosely("Successfully generated assembly", (config.printAssembly) ? assembly : null);
			
			return assembly;
		}
		catch (Exception e) {
			throw new CodeEmitterException("Failed to generate assembly", e);
		}
	}
}
