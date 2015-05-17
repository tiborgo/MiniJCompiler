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
package minij.assembler;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import minij.Configuration;
import minij.Logger;
import minij.MiniJCompiler;

import org.apache.commons.io.FilenameUtils;

public class Assembler {
	
	public static void assemble(Configuration config, String assembly) throws AssemblerException {
		
		try {
			new File(FilenameUtils.getPath(config.outputFile)).mkdirs();
			
			// -xc specifies the input language as C and is required for GCC to read from stdin
			ProcessBuilder processBuilder = new ProcessBuilder("gcc", "-o", config.outputFile, "-m32", "-xc", MiniJCompiler.RUNTIME_DIRECTORY.toString() + File.separator + "runtime_32.c", "-m32", "-xassembler", "-");
			Process gccCall = processBuilder.start();
			// Write C code to stdin of C Compiler
			OutputStream stdin = gccCall.getOutputStream();
			stdin.write(assembly.getBytes());
			stdin.close();

			gccCall.waitFor();

			// Print error messages of GCC
			if (gccCall.exitValue() != 0) {

				StringBuilder errOutput = new StringBuilder();
				InputStream stderr = gccCall.getErrorStream();
				String line;
				BufferedReader bufferedStderr = new BufferedReader(new InputStreamReader(stderr));
				while ((line = bufferedStderr.readLine()) != null) {
					errOutput.append(line + System.lineSeparator());
				}
				bufferedStderr.close();
				stderr.close();

				throw new AssemblerException("Failed to compile assembly:" + System.lineSeparator() + errOutput.toString());
			}

			Logger.logVerbosely("Successfully compiled assembly");
		}
		catch (IOException e) {
			throw new AssemblerException("Failed to transfer assembly to gcc", e);
		}
		catch (InterruptedException e) {
			throw new AssemblerException("Failed to invoke gcc", e);
		}

	}

}
