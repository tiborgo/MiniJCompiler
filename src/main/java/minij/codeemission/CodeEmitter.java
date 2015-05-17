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
