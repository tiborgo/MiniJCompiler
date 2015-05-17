package minij.ast.visitors;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import minij.MiniJLexer;
import minij.MiniJParser;
import minij.parse.rules.Program;
import minij.parse.visitors.ASTVisitor;
import minij.semanticanalysis.visitors.TypeCheckVisitor;
import minij.semanticanalysis.visitors.TypeInferenceVisitor;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Test;

public class TypeCheckVisitorTest {
	private static final Path EXAMPLE_PROGRAM_PATH_BASE = Paths.get("src/test/resources/minij-examples");
	private static final Path EXAMPLE_PROGRAM_PATH_WORKING = EXAMPLE_PROGRAM_PATH_BASE.resolve("working");
	private static final Path EXAMPLE_PROGRAM_PATH_FAILING = EXAMPLE_PROGRAM_PATH_BASE.resolve("typeErrors");

	@Test
	public void testVisitWorkingExamples() throws Exception {
		FileVisitor<Path> workingFilesVisitior = new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				System.out.println("Testing type checker with file \""+file.toString()+"\"");
				ANTLRFileStream reader = new ANTLRFileStream(file.toString());
				MiniJLexer lexer = new MiniJLexer((CharStream) reader);
				TokenStream tokens = new CommonTokenStream(lexer);
				MiniJParser parser = new MiniJParser(tokens);
				ParseTree parseTree = parser.prog();
				ASTVisitor astVisitor = new ASTVisitor();
				Program ast = (Program) astVisitor.visit(parseTree);
				TypeInferenceVisitor typeInferenceVisitor = new TypeInferenceVisitor();
				ast.accept(typeInferenceVisitor);
				TypeCheckVisitor visitor = new TypeCheckVisitor();
				boolean typesCorrect = ast.accept(visitor);
				if (!typesCorrect) {
					for (String error : visitor.getErrors()) {
						System.err.println(error);
					}
				}
				assertTrue(typesCorrect);
				return super.visitFile(file, attrs);
			}
		};
		Files.walkFileTree(EXAMPLE_PROGRAM_PATH_WORKING, workingFilesVisitior);
	}

	@Test
	public void testVisitTypeErrorExamples() throws Exception {
		FileVisitor<Path> failingFilesVisitior = new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				if (file.toString().endsWith("LinkedListBUG.java")) {
					return super.visitFile(file, attrs);
				}
				System.out.println("Testing type checker with file \""+file.toString()+"\"");
				ANTLRFileStream reader = new ANTLRFileStream(file.toString());
				MiniJLexer lexer = new MiniJLexer((CharStream) reader);
				TokenStream tokens = new CommonTokenStream(lexer);
				MiniJParser parser = new MiniJParser(tokens);
				ParseTree parseTree = parser.prog();
				ASTVisitor astVisitor = new ASTVisitor();
				Program ast = (Program) astVisitor.visit(parseTree);
				TypeInferenceVisitor typeInferenceVisitor = new TypeInferenceVisitor();
				ast.accept(typeInferenceVisitor);
				TypeCheckVisitor visitor = new TypeCheckVisitor();
				boolean typesCorrect = ast.accept(visitor);
				assertFalse("\"" + file.toString() + "\" passed type check but it shouldn't", typesCorrect);
				return super.visitFile(file, attrs);
			}
		};
		Files.walkFileTree(EXAMPLE_PROGRAM_PATH_FAILING, failingFilesVisitior);
	}
}
