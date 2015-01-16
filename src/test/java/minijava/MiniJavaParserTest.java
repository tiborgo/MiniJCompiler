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
import minijava.ast.rules.Program;
import minijava.ast.visitors.TypeCheckVisitor;
import minijava.ast.visitors.TypeInferenceVisitor;
import minijava.parsing_actions.ASTVisitor;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Test;

public class MiniJavaParserTest {
	private static final Path EXAMPLE_PROGRAM_PATH_BASE = Paths.get("src/test/resources/minijava-examples");
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
	
	@Test
	public void testTypeFailingExamples() throws IOException {
		FileVisitor<Path> typeFailingFilesVisitior = new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				System.out.println("Testing type for file \""+file.toString()+"\"");
				ANTLRFileStream antlrStream = new ANTLRFileStream(file.toString());
				MiniJavaLexer lexer = new MiniJavaLexer(antlrStream);
				TokenStream tokens = new CommonTokenStream(lexer);
				MiniJavaParser parser = new MiniJavaParser(tokens);
				
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
