package minij.codeemission;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;

import minij.Configuration;
import minij.Logger;
import minij.instructionselection.MachineSpecifics;
import minij.instructionselection.assems.Assem;
import minij.translate.layout.Fragment;

import org.apache.commons.io.FilenameUtils;

public class CodeEmitter {

	public static String emitCode(Configuration config, List<Fragment<List<Assem>>> assemFragments, MachineSpecifics machineSpecifics) throws CodeEmitterException {
		
		try {
			String assembly = machineSpecifics.printAssembly(assemFragments);;
			
			if (config.codeEmission) {
				new File(FilenameUtils.getPath(config.outputFile)).mkdirs();
				PrintWriter out = new PrintWriter(config.outputFile);
				out.print(assembly);
				out.close();
			}
			
			Logger.logVerbosely("Successfully generated assembly");
			
			if (config.printAssembly) {
				Logger.log(assembly);
			}
			
			return assembly;
		}
		catch (Exception e) {
			throw new CodeEmitterException("Failed to generate assembly", e);
		}
	}
}
