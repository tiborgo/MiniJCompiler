/*
 * MiniJCompiler: Compiler for a subset of the Java(R) language
 *
 * (C) Copyright 2014-2015 Tibor Goldschwendt <goldschwendt@cip.ifi.lmu.de>,
 * Michael Seifert <mseifert@error-reports.org>
 *
 * This file is part of MiniJCompiler.
 *
 * MiniJCompiler is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MiniJCompiler is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MiniJCompiler.  If not, see <http://www.gnu.org/licenses/>.
 */
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
