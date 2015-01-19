package minijava;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import minijava.parse.ParserException;
import minijava.semanticanalysis.SemanticAnalyserException;

import org.apache.commons.io.FileUtils;

public class TestFiles {

	public static final Path EXAMPLE_PROGRAM_PATH_BASE = Paths.get("src/test/resources/minijava-examples");
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
