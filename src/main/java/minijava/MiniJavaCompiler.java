package minijava;
import java.io.IOException;

import minijava.MiniJavaLexer;
import minijava.MiniJavaParser;
import minijava.syntax.ASTVisitor;
import minijava.syntax.ast.PrettyPrint;
import minijava.syntax.ast.Prg;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

//import MiniJavaPrettyPrintVisitor;

public class MiniJavaCompiler {

	public static void main(String[] args) {
		// TODO code application logic here
		// SymbolTable table = new SymbolTable();

		try {
			ANTLRFileStream reader = new ANTLRFileStream(
					"src/test/resources/minijava-examples/working/ArrSum.java");
			MiniJavaLexer lexer = new MiniJavaLexer((CharStream) reader);
			TokenStream tokens = new CommonTokenStream(lexer);
			MiniJavaParser parser = new MiniJavaParser(tokens);
			ParseTree tree = parser.prog();
			ASTVisitor astVisitor = new ASTVisitor();
			Prg program = (Prg) astVisitor.visit(tree);

			String output = PrettyPrint.prettyPrint(program);
			System.out.print(output);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Not Accepted");
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}
}