package minij.translate;

import java.util.List;

import minij.Configuration;
import minij.Logger;
import minij.instructionselection.MachineSpecifics;
import minij.parse.rules.Program;
import minij.translate.layout.FragmentProc;
import minij.translate.tree.TreeStm;
import minij.translate.visitors.IntermediatePrettyPrintVisitor;
import minij.translate.visitors.IntermediateVisitor;

public class Translator {

	public static List<FragmentProc<TreeStm>> translate(Configuration config, Program program, MachineSpecifics machineSpecifics) throws TranslatorException {

		try {
			IntermediateVisitor intermediateVisitor = new IntermediateVisitor(machineSpecifics, program);
			List<FragmentProc<TreeStm>> procFragements = program.accept(intermediateVisitor);

			Logger.logVerbosely("Successfully generated intermediate language");
			
			if (config.printIntermediate) {
				StringBuilder intermediateOutputBuilder = new StringBuilder();
				for (FragmentProc<TreeStm> frag : procFragements) {
					intermediateOutputBuilder
						.append(frag.body.accept(new IntermediatePrettyPrintVisitor()))
						.append(System.lineSeparator())
						.append("-----")
						.append(System.lineSeparator());
				}
				Logger.log(intermediateOutputBuilder.toString());
			}

			return procFragements;
		}
		catch (Exception e) {
			throw new TranslatorException("Failed to generate intermediate language", e);
		}
	}
}
