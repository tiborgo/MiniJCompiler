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
package minij;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import static org.junit.Assert.fail;
import minij.parse.rules.Program;
import minij.parse.visitors.ASTVisitor;
import minij.semanticanalysis.visitors.TypeCheckVisitor;
import minij.semanticanalysis.visitors.TypeInferenceVisitor;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Test;

public class MiniJParserTest {
	private static final Path EXAMPLE_PROGRAM_PATH_BASE = Paths.get("src/test/resources/minij-examples");
	private static final Path EXAMPLE_PROGRAM_PATH_WORKING = EXAMPLE_PROGRAM_PATH_BASE.resolve("working");
	private static final Path EXAMPLE_PROGRAM_PATH_FAILING = EXAMPLE_PROGRAM_PATH_BASE.resolve("parseErrors");
	private static final Path EXAMPLE_PROGRAM_PATH_TYPE_FAILING = EXAMPLE_PROGRAM_PATH_BASE.resolve("typeErrors");

	@Test
	public void testParseWorkingExamples() throws IOException {
		FileVisitor<Path> workingFilesVisitior = new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				System.out.println("Testing parser input from file \""+file.toString()+"\"");
				ANTLRFileStream antlrStream = new ANTLRFileStream(file.toString());
				MiniJLexer lexer = new MiniJLexer(antlrStream);
				TokenStream tokens = new CommonTokenStream(lexer);
				MiniJParser parser = new MiniJParser(tokens);
				parser.setErrorHandler(new BailErrorStrategy());
				parser.prog();
				return super.visitFile(file, attrs);
			}
		};
		Files.walkFileTree(EXAMPLE_PROGRAM_PATH_WORKING, workingFilesVisitior);
	}

	@Test
	public void testParseFailingExamples() throws IOException {
		FileVisitor<Path> workingFilesVisitior = new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				System.out.println("Testing parser input from file \""+file.toString()+"\"");
				ANTLRFileStream antlrStream = new ANTLRFileStream(file.toString());
				MiniJLexer lexer = new MiniJLexer(antlrStream);
				TokenStream tokens = new CommonTokenStream(lexer);
				MiniJParser parser = new MiniJParser(tokens);
				parser.setErrorHandler(new BailErrorStrategy());
				/*
				 * Catch all exceptions first, to ensure that every single
				 * compilation unit exits with an Exception. Otherwise, this
				 * method will return after the first piece of code.
				 */
				try {
					parser.prog();
					fail("The example "+file.toString()+" should have failed, but was accepted by the parser.");
				} catch (ParseCancellationException e) {
				}
				return super.visitFile(file, attrs);
			}
		};
		Files.walkFileTree(EXAMPLE_PROGRAM_PATH_FAILING, workingFilesVisitior);
	}
	
	@Test
	public void testTypeFailingExamples() throws IOException {
		FileVisitor<Path> typeFailingFilesVisitior = new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				System.out.println("Testing type for file \""+file.toString()+"\"");
				ANTLRFileStream antlrStream = new ANTLRFileStream(file.toString());
				MiniJLexer lexer = new MiniJLexer(antlrStream);
				TokenStream tokens = new CommonTokenStream(lexer);
				MiniJParser parser = new MiniJParser(tokens);
				
				ParseTree tree = parser.prog();
				ASTVisitor astVisitor = new ASTVisitor();
				Program program = (Program) astVisitor.visit(tree);
				TypeInferenceVisitor typeInferenceVisitor = new TypeInferenceVisitor();
				program.accept(typeInferenceVisitor);
				
				TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
				if (program.accept(typeCheckVisitor)) {
					fail("The example "+file.toString()+" should have failed, but was accepted by the type checker.");
				}
				
				return super.visitFile(file, attrs);
			}
		};
		Files.walkFileTree(EXAMPLE_PROGRAM_PATH_TYPE_FAILING, typeFailingFilesVisitior);
	}
}
