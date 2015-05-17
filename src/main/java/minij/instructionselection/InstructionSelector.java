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
