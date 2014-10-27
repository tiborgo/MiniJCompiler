package minijava;
import java.io.IOException;

import minijava.MiniJavaLexer;
import minijava.MiniJavaParser;

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
					"src/test/resources/minijava-examples/small/ArrSum.java");
			MiniJavaLexer lexer = new MiniJavaLexer((CharStream) reader);
			TokenStream tokens = new CommonTokenStream(lexer);
			MiniJavaParser parser = new MiniJavaParser(tokens);
			ParseTree tree = parser.prog();

			MiniJavaPrettyPrintVisitor visitor = new MiniJavaPrettyPrintVisitor();
			System.out.print(visitor.visit(tree));
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Not Accepted");
		} catch (IllegalArgumentException e) {
			System.out.println(e.getMessage());
		}
	}
}