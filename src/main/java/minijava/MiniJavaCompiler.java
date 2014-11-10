package minijava;
import java.io.IOException;

import minijava.antlr.visitors.ASTVisitor;
import minijava.ast.rules.Prg;
import minijava.ast.visitors.PrettyPrintVisitor;
import minijava.ast.visitors.SymbolTableVisitor;
import minijava.ast.visitors.TypeCheckVisitor;
import minijava.symboltable.tree.Program;
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class MiniJavaCompiler implements Frontend {

	@Override
	public Prg getAbstractSyntaxTree(String filePath) throws IOException {
		ANTLRFileStream reader = new ANTLRFileStream(filePath);
		MiniJavaLexer lexer = new MiniJavaLexer((CharStream) reader);
		TokenStream tokens = new CommonTokenStream(lexer);
		MiniJavaParser parser = new MiniJavaParser(tokens);
		ParseTree parseTree = parser.prog();
		ASTVisitor astVisitor = new ASTVisitor();
		Prg program = (Prg) astVisitor.visit(parseTree);
		return program;
	}

	public static void main(String[] args) {
		// TODO code application logic here
		// SymbolTable table = new SymbolTable();

		MiniJavaCompiler compiler = new MiniJavaCompiler();
		try {
			Prg program = compiler.getAbstractSyntaxTree("src/test/resources/minijava-examples/working/ArrSum.java");
			PrettyPrintVisitor prettyPrintVisitor = new PrettyPrintVisitor("");
			String output = program.accept(prettyPrintVisitor);
			System.out.print(output);
			
			SymbolTableVisitor symbolTableVisitor = new SymbolTableVisitor(); 
			Program symbolTable = program.accept(symbolTableVisitor);
			
			TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor(symbolTable);
			if (program.accept(typeCheckVisitor)) {
				System.out.println("No Type Errors");
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Not Accepted");
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}
}