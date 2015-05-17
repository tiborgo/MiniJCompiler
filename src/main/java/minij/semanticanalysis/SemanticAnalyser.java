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
package minij.semanticanalysis;

import java.util.ArrayList;

import minij.Configuration;
import minij.Logger;
import minij.parse.rules.Program;
import minij.semanticanalysis.visitors.TypeCheckVisitor;
import minij.semanticanalysis.visitors.TypeInferenceVisitor;

public class SemanticAnalyser {

	public static Program analyseSemantics(Configuration config, Program program) throws SemanticAnalyserException {
		
		Program typedProgram = new Program(new ArrayList<>(program.getClasses()));
		
		// infer types
		try {
			program.accept(new TypeInferenceVisitor());
		}
		catch (Exception e) {
			throw new SemanticAnalyserException("Failed to create symbol table", e);
		}
		
		Logger.logVerbosely("Successfully built symbol table");
		
		// check types
		try {
			if (program.accept(new TypeCheckVisitor())) {
	
			}
			else {
				throw new Exception("Type check failed");
			}
		}
		catch (Exception e) {
			throw new SemanticAnalyserException("Type check failed", e);
		}
		
		Logger.logVerbosely("Successfully checked types");
		
		return typedProgram;	
	}
}
