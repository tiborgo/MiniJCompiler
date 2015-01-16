package minijava;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import minijava.backend.i386.I386MachineSpecifics;
import minijava.intermediate.Label;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class MiniJavaCompilerTest {

	private static final Path EXAMPLE_PROGRAM_PATH_BASE = Paths.get("src/test/resources/minijava-examples");
	private static final Path EXAMPLE_PROGRAM_PATH_WORKING = EXAMPLE_PROGRAM_PATH_BASE.resolve("working");
	private static final Path EXAMPLE_PROGRAM_PATH_FAILING = EXAMPLE_PROGRAM_PATH_BASE.resolve("parseErrors");
	private static final Path EXAMPLE_PROGRAM_PATH_TYPE_FAILING = EXAMPLE_PROGRAM_PATH_BASE.resolve("typeErrors");
	private static final Path EXAMPLE_PROGRAM_PATH_RUNTIME_FAILING = EXAMPLE_PROGRAM_PATH_BASE.resolve("runtimeErrors");

	@Parameterized.Parameters
	public static Collection<Object[]> files() throws IOException {

		final List<Object[]> files = new LinkedList<>();

		FileVisitor<Path> workingFilesVisitior = new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				files.add(new Object[] { file, true });
				return super.visitFile(file, attrs);
			}
		};
		Files.walkFileTree(EXAMPLE_PROGRAM_PATH_WORKING, workingFilesVisitior);
		Files.walkFileTree(EXAMPLE_PROGRAM_PATH_RUNTIME_FAILING, workingFilesVisitior);
		
		FileVisitor<Path> failingFilesVisitior = new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				files.add(new Object[] { file, false });
				return super.visitFile(file, attrs);
			}
		};
		Files.walkFileTree(EXAMPLE_PROGRAM_PATH_FAILING, failingFilesVisitior);
		Files.walkFileTree(EXAMPLE_PROGRAM_PATH_TYPE_FAILING, failingFilesVisitior);

		return files;
	}

	@BeforeClass
	public static void setUp() {
		String osName = System.getProperty("os.name").toLowerCase();
		if (osName.contains("mac")) {
			Label.leadingUnderscore = true;
		} else {
			Label.leadingUnderscore = false;
		}
	}

	private Path file;
	private boolean works;
	
	public MiniJavaCompilerTest(Path file, boolean works) {
		this.file = file;
		this.works = works;
	}
	
	@Test
	public void testCompileWorkingExamples() throws IOException {

		System.out.println("Testing compiler input from file \"" + file.toString() + "\"");
		
		Configuration.initialize(new String[]{file.toString()});
		
		MiniJavaCompiler compiler = new MiniJavaCompiler(new I386MachineSpecifics());

		try {
			compiler.compile();
			if (!works) {
				fail("The example " + file.toString() + " should have failed, but was accepted by the compiler.");
			}
		}
		catch (Exception e) {
			if (works) {
				fail("The example " + file.toString() + " should have been accepted by the compiler but failed: " + e.getMessage());
			}
		}
	}
}
