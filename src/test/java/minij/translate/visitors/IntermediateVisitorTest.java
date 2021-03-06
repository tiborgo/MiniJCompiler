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
package minij.translate.visitors;

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
import minij.MiniJLexer;
import minij.MiniJParser;
import minij.backend.dummymachine.DummyMachineSpecifics;
import minij.backend.dummymachine.IntermediateToCmm;
import minij.canonicalize.visitors.CanonVisitor;
import minij.parse.rules.Program;
import minij.parse.visitors.ASTVisitor;
import minij.semanticanalysis.visitors.TypeInferenceVisitor;
import minij.translate.layout.FragmentProc;
import minij.translate.tree.TreeStm;
import minij.translate.visitors.IntermediateVisitor;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Before;
import org.junit.Test;

public class IntermediateVisitorTest {
	private static final Path EXAMPLE_PROGRAM_PATH_BASE = Paths.get("src/test/resources/minij-examples");
	private static final Path EXAMPLE_PROGRAM_PATH_WORKING = EXAMPLE_PROGRAM_PATH_BASE.resolve("working");
	private static final File RUNTIME_DIRECTORY = new File("src/main/resources/minij/runtime").getAbsoluteFile();

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
				MiniJLexer lexer = new MiniJLexer((CharStream) reader);
				TokenStream tokens = new CommonTokenStream(lexer);
				MiniJParser parser = new MiniJParser(tokens);
				ParseTree parseTree = parser.prog();
				ASTVisitor astVisitor = new ASTVisitor();
				Program ast = (Program) astVisitor.visit(parseTree);
				TypeInferenceVisitor typeInferenceVisitor = new TypeInferenceVisitor();
				ast.accept(typeInferenceVisitor);

				visitor = new IntermediateVisitor(new DummyMachineSpecifics(), ast);
				List<FragmentProc<TreeStm>> fragmentList = ast.accept(visitor);
				// TODO: Remove canonicalization step from test
				List<FragmentProc<List<TreeStm>>> fragmentListCanonicalized = new ArrayList<>(fragmentList.size());
				for (FragmentProc<TreeStm> fragment : fragmentList) {
					fragmentListCanonicalized.add((FragmentProc<List<TreeStm>>) fragment.accept(new CanonVisitor()));
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
					fail("C Compiler returned with value " + retVal);
				}
				return super.visitFile(file, attrs);
			}
		};
		Files.walkFileTree(EXAMPLE_PROGRAM_PATH_WORKING, workingFilesVisitior);
	}
}