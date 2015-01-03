package minijava.symboltable.visitors;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import minijava.MiniJavaLexer;
import minijava.MiniJavaParser;
import minijava.antlr.visitors.ASTVisitor;
import minijava.ast.rules.Prg;
import minijava.symboltable.tree.Program;
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Before;
import org.junit.Test;

public class CreateSymbolTableVisitorTest {
	private static final Path EXAMPLE_PROGRAM_PATH_BASE = Paths.get("src/test/resources/minijava-examples");
	private static final Path EXAMPLE_PROGRAM_PATH_WORKING = EXAMPLE_PROGRAM_PATH_BASE.resolve("working");
	private static final Path EXAMPLE_PROGRAM_PATH_FAILING = EXAMPLE_PROGRAM_PATH_BASE.resolve("typeErrors");

	private static CreateSymbolTableVisitor visitor;

	@Before
	public void setUp() throws Exception {
		visitor = new CreateSymbolTableVisitor();
	}

	@Test
	public void testVisitWorkingExamples() throws IOException {
		FileVisitor<Path> workingFilesVisitior = new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				System.out.println("Testing creation of symbol table for file \""+file.toString()+"\"");
				ANTLRFileStream reader = new ANTLRFileStream(file.toString());
				MiniJavaLexer lexer = new MiniJavaLexer((CharStream) reader);
				TokenStream tokens = new CommonTokenStream(lexer);
				MiniJavaParser parser = new MiniJavaParser(tokens);
				ParseTree parseTree = parser.prog();
				ASTVisitor astVisitor = new ASTVisitor();
				Prg ast = (Prg) astVisitor.visit(parseTree);
				Program symbolTable = ast.accept(visitor);
				// TODO: Check contents of symbol table
				return super.visitFile(file, attrs);
			}
		};
		Files.walkFileTree(EXAMPLE_PROGRAM_PATH_WORKING, workingFilesVisitior);
	}
}