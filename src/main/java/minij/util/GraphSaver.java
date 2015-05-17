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
