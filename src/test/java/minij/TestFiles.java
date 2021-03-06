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
package minij;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import minij.RunOutputException;
import minij.parse.ParserException;
import minij.semanticanalysis.SemanticAnalyserException;

import org.apache.commons.io.FileUtils;

public class TestFiles {

	public static final Path EXAMPLE_PROGRAM_PATH_BASE = Paths.get("src/test/resources/minij-examples");
	public static final Path EXAMPLE_PROGRAM_PATH_WORKING = EXAMPLE_PROGRAM_PATH_BASE.resolve("working");
	public static final Path EXAMPLE_PROGRAM_PATH_PARSE_FAILING = EXAMPLE_PROGRAM_PATH_BASE.resolve("parseErrors");
	public static final Path EXAMPLE_PROGRAM_PATH_TYPE_FAILING = EXAMPLE_PROGRAM_PATH_BASE.resolve("typeErrors");
	public static final Path EXAMPLE_PROGRAM_PATH_RUNTIME_FAILING = EXAMPLE_PROGRAM_PATH_BASE.resolve("runtimeErrors");
	
	private static List<Object[]> getFiles(Class<? extends Exception> exceptionClass, Path... paths) {
		List<Object[]> files = new LinkedList<>();
		
		for (Path path : paths) {
			for (File file : FileUtils.listFiles(path.toFile(), null, true)) {
				files.add(new Object[] { file, exceptionClass });
			}
		}

		return files;
	}
	
	public static List<Object[]> getFiles() {
		List<Object[]> files = new LinkedList<>();
		files.addAll(getFiles(null, EXAMPLE_PROGRAM_PATH_WORKING));
		files.addAll(getFiles(ParserException.class, EXAMPLE_PROGRAM_PATH_PARSE_FAILING));
		files.addAll(getFiles(SemanticAnalyserException.class, EXAMPLE_PROGRAM_PATH_TYPE_FAILING));
		files.addAll(getFiles(RunOutputException.class, EXAMPLE_PROGRAM_PATH_RUNTIME_FAILING));
		return files;
	}
}
