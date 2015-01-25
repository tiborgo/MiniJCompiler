package minijava.assembler;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import minijava.Configuration;
import minijava.Logger;
import minijava.MiniJavaCompiler;

import org.apache.commons.io.FilenameUtils;

public class Assembler {
	
	public static void assemble(Configuration config, String assembly) throws AssemblerException {
		
		try {
			new File(FilenameUtils.getPath(config.outputFile)).mkdirs();
			
			// -xc specifies the input language as C and is required for GCC to read from stdin
			ProcessBuilder processBuilder = new ProcessBuilder("gcc", "-o", config.outputFile, "-m32", "-xc", MiniJavaCompiler.RUNTIME_DIRECTORY.toString() + File.separator + "runtime_32.c", "-m32", "-xassembler", "-");
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
