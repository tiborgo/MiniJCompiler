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

import minijava.backend.i386.I386MachineSpecifics;
import minijava.intermediate.Label;

import org.junit.BeforeClass;
import org.junit.Test;

public class MiniJavaCompilerTest {
	private static final Path EXAMPLE_PROGRAM_PATH_BASE = Paths.get("src/test/resources/minijava-examples");
	private static final Path EXAMPLE_PROGRAM_PATH_WORKING = EXAMPLE_PROGRAM_PATH_BASE.resolve("working");
	private static final Path EXAMPLE_PROGRAM_PATH_FAILING = EXAMPLE_PROGRAM_PATH_BASE.resolve("parseErrors");
	private static final Path EXAMPLE_PROGRAM_PATH_TYPE_FAILING = EXAMPLE_PROGRAM_PATH_BASE.resolve("typeErrors");
	private static final Path EXAMPLE_PROGRAM_PATH_RUNTIME_FAILING = EXAMPLE_PROGRAM_PATH_BASE.resolve("runtimeErrors");
	
	@BeforeClass
	public static void setUp() {
		String osName = System.getProperty("os.name").toLowerCase();
		if (osName.contains("mac")) {
			Label.leadingUnderscore = true;
		}
		else {
			Label.leadingUnderscore = false;
		}
	}

	@Test
	public void testCompileWorkingExamples() throws IOException {
		FileVisitor<Path> workingFilesVisitior = new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				System.out.println("Testing compiler input from file \""+file.toString()+"\"");
				MiniJavaCompiler compiler = new MiniJavaCompiler(new I386MachineSpecifics());
				compiler.inputFile = file.toString();
				try {
					compiler.compile("gcc");
				}
				catch (Exception e) {
					fail("The example "+file.toString()+" should have been accepted by the compiler but failed: " + e.getMessage());
				}
				return super.visitFile(file, attrs);
			}
		};
		Files.walkFileTree(EXAMPLE_PROGRAM_PATH_WORKING, workingFilesVisitior);
		Files.walkFileTree(EXAMPLE_PROGRAM_PATH_RUNTIME_FAILING, workingFilesVisitior);
	}

	@Test
	public void testCompileFailingExamples() throws IOException {
		FileVisitor<Path> workingFilesVisitior = new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				System.out.println("Testing parser input from file \""+file.toString()+"\"");
				System.out.println("Testing compiler input from file \""+file.toString()+"\"");
				MiniJavaCompiler compiler = new MiniJavaCompiler(new I386MachineSpecifics());
				compiler.inputFile = file.toString();
				/*
				 * Catch all exceptions first, to ensure that every single
				 * compilation unit exits with an Exception. Otherwise, this
				 * method will return after the first piece of code.
				 */
				try {
					compiler.compile("gcc");
					fail("The example "+file.toString()+" should have failed, but was accepted by the compiler.");
				} catch (Exception e) {
				}
				return super.visitFile(file, attrs);
			}
		};
		Files.walkFileTree(EXAMPLE_PROGRAM_PATH_FAILING, workingFilesVisitior);
		Files.walkFileTree(EXAMPLE_PROGRAM_PATH_TYPE_FAILING, workingFilesVisitior);
	}
}
