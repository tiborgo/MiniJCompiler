package minijava.ast.visitors;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import static org.junit.Assert.assertTrue;

import minijava.Frontend;
import minijava.MiniJavaCompiler;
import minijava.ast.rules.Prg;
import minijava.symboltable.tree.Program;
import org.junit.BeforeClass;
import org.junit.Test;

public class TypeCheckVisitorTest {
	private static final Path EXAMPLE_PROGRAM_PATH_BASE = Paths.get("src/test/resources/minijava-examples");
	private static final Path EXAMPLE_PROGRAM_PATH_WORKING = EXAMPLE_PROGRAM_PATH_BASE.resolve("working");
	private static final Path EXAMPLE_PROGRAM_PATH_FAILING = EXAMPLE_PROGRAM_PATH_BASE.resolve("parseErrors");

	private static Frontend miniJavaFrontend;

	@BeforeClass
	public static void setUpBeforeClass() {
		miniJavaFrontend = new MiniJavaCompiler();
	}

	@Test
	public void testVisitWorkingExamples() throws Exception {
		FileVisitor<Path> workingFilesVisitior = new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				System.out.println("Testing creation of symbol table for file \""+file.toString()+"\"");
				Prg ast = miniJavaFrontend.getAbstractSyntaxTree(file.toString());
				SymbolTableVisitor symbolTableVisitor = new SymbolTableVisitor();
				Program symbolTable = symbolTableVisitor.visit(ast);
				TypeCheckVisitor visitor = new TypeCheckVisitor(symbolTable);
				boolean typesCorrect = ast.accept(visitor);
				assertTrue(typesCorrect);
				return super.visitFile(file, attrs);
			}
		};
		Files.walkFileTree(EXAMPLE_PROGRAM_PATH_WORKING, workingFilesVisitior);
	}
}