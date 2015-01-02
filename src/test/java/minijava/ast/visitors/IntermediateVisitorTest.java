package minijava.ast.visitors;

import java.io.BufferedReader;
import java.io.File;
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
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.fail;

import minijava.MiniJavaLexer;
import minijava.MiniJavaParser;
import minijava.antlr.visitors.ASTVisitor;
import minijava.ast.rules.Prg;
import minijava.backend.dummymachine.DummyMachineSpecifics;
import minijava.backend.dummymachine.IntermediateToCmm;
import minijava.intermediate.FragmentProc;
import minijava.intermediate.canon.Canon;
import minijava.intermediate.tree.TreeStm;
import minijava.symboltable.tree.Program;
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Before;
import org.junit.Test;

public class IntermediateVisitorTest {
	private static final Path EXAMPLE_PROGRAM_PATH_BASE = Paths.get("src/test/resources/minijava-examples");
	private static final Path EXAMPLE_PROGRAM_PATH_WORKING = EXAMPLE_PROGRAM_PATH_BASE.resolve("working");
	private static final File RUNTIME_DIRECTORY = new File("src/main/resources/minijava/runtime").getAbsoluteFile();

	private static IntermediateVisitor visitor;

	@Before
	public void setUp() {
		visitor = null;
	}

	@Test
	public void testVisitWorkingExamples() throws IOException {
		FileVisitor<Path> workingFilesVisitior = new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				System.out.println("Testing creation translation to intermediate language for file \""+file.toString()+"\"");
				ANTLRFileStream reader = new ANTLRFileStream(file.toString());
				MiniJavaLexer lexer = new MiniJavaLexer((CharStream) reader);
				TokenStream tokens = new CommonTokenStream(lexer);
				MiniJavaParser parser = new MiniJavaParser(tokens);
				ParseTree parseTree = parser.prog();
				ASTVisitor astVisitor = new ASTVisitor();
				Prg ast = (Prg) astVisitor.visit(parseTree);

				Program symbolTable = ast.accept(new SymbolTableVisitor());
				visitor = new IntermediateVisitor(new DummyMachineSpecifics(), symbolTable);
				List<FragmentProc<TreeStm>> fragmentList = ast.accept(visitor);
				// TODO: Remove canonicalization step from test
				List<FragmentProc<List<TreeStm>>> fragmentListCanonicalized = new ArrayList<>(fragmentList.size());
				for (FragmentProc<TreeStm> fragment : fragmentList) {
					fragmentListCanonicalized.add((FragmentProc<List<TreeStm>>) fragment.accept(new Canon()));
				}
				String cCode = IntermediateToCmm.stmListFragmentsToCmm(fragmentListCanonicalized);
				// -xc specifies the input language as C and is required for GCC to read from stdin
				ProcessBuilder processBuilder = new ProcessBuilder("gcc", "-o", "/dev/null", "-xc", "-m32", "runtime_32.c", "-");
				processBuilder.directory(RUNTIME_DIRECTORY);
				Process gccCall = processBuilder.start();
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