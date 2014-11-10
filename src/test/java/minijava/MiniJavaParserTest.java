package minijava;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import static org.junit.Assert.fail;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.junit.Test;

public class MiniJavaParserTest {
	private static final Path EXAMPLE_PROGRAM_PATH_BASE = Paths.get("src/test/resources/minijava-examples");
	private static final Path EXAMPLE_PROGRAM_PATH_WORKING = EXAMPLE_PROGRAM_PATH_BASE.resolve("working");
	private static final Path EXAMPLE_PROGRAM_PATH_FAILING = EXAMPLE_PROGRAM_PATH_BASE.resolve("parseErrors");

	@Test
	public void testParseWorkingExamples() throws IOException {
		FileVisitor<Path> workingFilesVisitior = new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				System.out.println("Testing parser input from file \""+file.toString()+"\"");
				ANTLRFileStream antlrStream = new ANTLRFileStream(file.toString());
				MiniJavaLexer lexer = new MiniJavaLexer(antlrStream);
				TokenStream tokens = new CommonTokenStream(lexer);
				MiniJavaParser parser = new MiniJavaParser(tokens);
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
				MiniJavaLexer lexer = new MiniJavaLexer(antlrStream);
				TokenStream tokens = new CommonTokenStream(lexer);
				MiniJavaParser parser = new MiniJavaParser(tokens);
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
}
