package minijava;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class TestFiles {

	public static final Path EXAMPLE_PROGRAM_PATH_BASE = Paths.get("src/test/resources/minijava-examples");
	public static final Path EXAMPLE_PROGRAM_PATH_WORKING = EXAMPLE_PROGRAM_PATH_BASE.resolve("working");
	public static final Path EXAMPLE_PROGRAM_PATH_PARSE_FAILING = EXAMPLE_PROGRAM_PATH_BASE.resolve("parseErrors");
	public static final Path EXAMPLE_PROGRAM_PATH_TYPE_FAILING = EXAMPLE_PROGRAM_PATH_BASE.resolve("typeErrors");
	public static final Path EXAMPLE_PROGRAM_PATH_RUNTIME_FAILING = EXAMPLE_PROGRAM_PATH_BASE.resolve("runtimeErrors");
	
	private static List<Object[]> getFiles(boolean work, Path... paths) {
		List<Object[]> files = new LinkedList<>();
		
		for (Path path : paths) {
			for (File file : FileUtils.listFiles(path.toFile(), null, true)) {
				files.add(new Object[] { file, work });
			}
		}

		return files;
	}
	
	public static List<Object[]> getWorkingFiles(Path... paths) {
		return getFiles(true, paths);
	}
	
	public static List<Object[]> getFailingFiles(Path... paths) {
		return getFiles(false, paths);
	}
}
