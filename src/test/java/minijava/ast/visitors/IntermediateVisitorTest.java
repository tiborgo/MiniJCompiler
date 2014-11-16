package minijava.ast.visitors;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

import static org.junit.Assert.fail;

import minijava.Frontend;
import minijava.MiniJavaCompiler;
import minijava.ast.rules.Prg;
import minijava.backend.dummymachine.DummyMachineSpecifics;
import minijava.backend.dummymachine.IntermediateToCmm;
import minijava.intermediate.Fragment;
import minijava.intermediate.tree.TreeStm;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class IntermediateVisitorTest {
	private static final Path EXAMPLE_PROGRAM_PATH_BASE = Paths.get("src/test/resources/minijava-examples");
	private static final Path EXAMPLE_PROGRAM_PATH_WORKING = EXAMPLE_PROGRAM_PATH_BASE.resolve("working");

	private static IntermediateVisitor visitor;
	private static Frontend miniJavaFrontend;

	@BeforeClass
	public static void setUpBeforeClass() {
		miniJavaFrontend = new MiniJavaCompiler();
	}

	@Before
	public void setUp() {
		visitor = new IntermediateVisitor(new DummyMachineSpecifics());
	}

	@Test
	public void testVisitWorkingExamples() throws IOException {
		FileVisitor<Path> workingFilesVisitior = new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				System.out.println("Testing creation translation to intermediate language for file \""+file.toString()+"\"");
				Prg ast = miniJavaFrontend.getAbstractSyntaxTree(file.toString());
				List<Fragment<TreeStm>> fragmentList = ast.accept(visitor);
				String cCode = IntermediateToCmm.stmFragmentsToCmm(fragmentList);
				Runtime runtime = Runtime.getRuntime();
				// -xc specifies the input language as C and is required for GCC to read from stdin
				Process gccCall = runtime.exec("gcc -o /dev/null -xc -");
				// Write C code to stdin of C Compiler
				OutputStream stdin = gccCall.getOutputStream();
				stdin.write(cCode.getBytes());
				stdin.close();

				try {
					gccCall.waitFor();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				// Print error messages of GCC
				InputStream stderr = gccCall.getErrorStream();
				BufferedReader bufferedStderr = new BufferedReader(new InputStreamReader(stderr));
				String line;
				while ((line = bufferedStderr.readLine()) != null) {
					System.out.println(line);
				}
				bufferedStderr.close();
				stderr.close();

				int retVal = gccCall.exitValue();
				if (retVal != 0) {
					System.out.println(cCode);
					fail("C Compiler returned with value "+retVal);
				}
				return super.visitFile(file, attrs);
			}
		};
		Files.walkFileTree(EXAMPLE_PROGRAM_PATH_WORKING, workingFilesVisitior);
	}
}