package minijava.parse;

import java.io.IOException;

import minijava.Configuration;
import minijava.Logger;
import minijava.MiniJavaLexer;
import minijava.MiniJavaParser;
import minijava.parse.rules.Program;
import minijava.parse.visitors.ASTVisitor;
import minijava.parse.visitors.PrettyPrintVisitor;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class Parser {
	
	public static Program parse(Configuration config) throws ParserException {
		try {
			ANTLRFileStream reader = new ANTLRFileStream(config.inputFile);
			MiniJavaLexer lexer = new MiniJavaLexer((CharStream) reader);
			TokenStream tokens = new CommonTokenStream(lexer);
			MiniJavaParser parser = new MiniJavaParser(tokens);
			ParseTree parseTree = parser.prog();
			ASTVisitor astVisitor = new ASTVisitor();
			Program program = (Program) astVisitor.visit(parseTree);
			
			String sourceCodeOutput = null;
			if (config.printSourceCode) {
				sourceCodeOutput = program.accept(new PrettyPrintVisitor(""));
			}

			Logger.logVerbosely("Successfully parsed input file", sourceCodeOutput);
			
			return program;
		}
		catch (IOException e) {
			throw new ParserException("Antlr could not open file " + config.inputFile, e);
		}
	}
}
