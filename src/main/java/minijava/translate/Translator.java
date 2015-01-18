package minijava.translate;

import java.util.List;

import minijava.Configuration;
import minijava.Logger;
import minijava.backend.MachineSpecifics;
import minijava.parse.rules.Program;
import minijava.translate.layout.FragmentProc;
import minijava.translate.tree.TreeStm;
import minijava.translate.visitors.IntermediatePrettyPrintVisitor;
import minijava.translate.visitors.IntermediateVisitor;

public class Translator {

	public static List<FragmentProc<TreeStm>> translate(Configuration config, Program program, MachineSpecifics machineSpecifics) throws TranslatorException {

		try {
			IntermediateVisitor intermediateVisitor = new IntermediateVisitor(machineSpecifics, program);
			List<FragmentProc<TreeStm>> procFragements = program.accept(intermediateVisitor);

			String intermediateOutput = null;
			if (config.printIntermediate) {
				StringBuilder intermediateOutputBuilder = new StringBuilder();
				for (FragmentProc<TreeStm> frag : procFragements) {
					intermediateOutputBuilder
						.append(frag.body.accept(new IntermediatePrettyPrintVisitor()))
						.append(System.lineSeparator())
						.append("-----")
						.append(System.lineSeparator());
				}
				intermediateOutput = intermediateOutputBuilder.toString();
			}

			Logger.logVerbosely("Successfully generated intermediate language", intermediateOutput);

			return procFragements;
		}
		catch (Exception e) {
			throw new TranslatorException("Failed to generate intermediate language", e);
		}
	}
}
