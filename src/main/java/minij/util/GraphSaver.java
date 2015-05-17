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
package minij.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class GraphSaver {
	
	public static <T> void saveGraph(SimpleGraph<T> graph) {
		
		try {
			ProcessBuilder processBuilder = new ProcessBuilder("/usr/local/Cellar/graphviz/2.38.0/bin/dot", "-o", "graph.pdf", "-Tpdf");
			//processBuilder.directory(MiniJCompiler.RUNTIME_DIRECTORY.toFile());
			Process dotCall = processBuilder.start();
			// Write C code to stdin of C Compiler
			OutputStream stdin = dotCall.getOutputStream();
			stdin.write(graph.getDot().getBytes());
			stdin.close();

			dotCall.waitFor();

			// Print error messages of GCC
			if (dotCall.exitValue() != 0) {

				StringBuilder errOutput = new StringBuilder();
				InputStream stderr = dotCall.getErrorStream();
				String line;
				BufferedReader bufferedStderr = new BufferedReader(new InputStreamReader(stderr));
				while ((line = bufferedStderr.readLine()) != null) {
					errOutput.append(line + System.lineSeparator());
				}
				bufferedStderr.close();
				stderr.close();

				throw new RuntimeException("Failed to create graph pdf:" + System.lineSeparator() + errOutput.toString());
			}
		}
		catch (IOException e) {
			throw new RuntimeException("Failed to transfer dot code to dot", e);
		}
		catch (InterruptedException e) {
			throw new RuntimeException("Failed to invoke dot", e);
		}
	}
}
