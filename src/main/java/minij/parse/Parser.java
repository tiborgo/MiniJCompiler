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
package minij.parse;

import java.io.IOException;
import java.util.BitSet;

import minij.Configuration;
import minij.Logger;
import minij.MiniJLexer;
import minij.MiniJParser;
import minij.parse.rules.Program;
import minij.parse.visitors.ASTVisitor;
import minij.parse.visitors.PrettyPrintVisitor;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTree;

public class Parser {
	
	private static class ParserErrorListener implements ANTLRErrorListener {
		
		private boolean errorOccurred = false;

		@Override
		public void syntaxError(Recognizer<?, ?> recognizer,
				Object offendingSymbol, int line, int charPositionInLine,
				String msg, RecognitionException e) {
			errorOccurred = true;
		}

		@Override
		public void reportAmbiguity(org.antlr.v4.runtime.Parser recognizer,
				DFA dfa, int startIndex, int stopIndex, boolean exact,
				BitSet ambigAlts, ATNConfigSet configs) {
			errorOccurred = true;
		}

		@Override
		public void reportAttemptingFullContext(
				org.antlr.v4.runtime.Parser recognizer, DFA dfa,
				int startIndex, int stopIndex, BitSet conflictingAlts,
				ATNConfigSet configs) {
			errorOccurred = true;
		}

		@Override
		public void reportContextSensitivity(
				org.antlr.v4.runtime.Parser recognizer, DFA dfa,
				int startIndex, int stopIndex, int prediction,
				ATNConfigSet configs) {
			errorOccurred = true;
		}
		
		public boolean didErrorOccurr() {
			return errorOccurred;
		}
		
	}
	
	public static Program parse(Configuration config) throws ParserException {
		try {
			ANTLRFileStream reader = new ANTLRFileStream(config.inputFile);
			MiniJLexer lexer = new MiniJLexer((CharStream) reader);
			TokenStream tokens = new CommonTokenStream(lexer);
			
			MiniJParser parser = new MiniJParser(tokens);
			ParserErrorListener listener = new ParserErrorListener();
			parser.addErrorListener(listener);
			ParseTree parseTree = parser.prog();
			if (listener.didErrorOccurr()) {
				throw new ParserException("Could not parse file");
			}
			
			ASTVisitor astVisitor = new ASTVisitor();
			Program program = (Program) astVisitor.visit(parseTree);
			
			Logger.logVerbosely("Successfully parsed input file");
			
			if (config.printSourceCode) {
				Logger.log(program.accept(new PrettyPrintVisitor("")));
			}
			
			return program;
		}
		catch (IOException e) {
			throw new ParserException("Antlr could not open file " + config.inputFile, e);
		}
		catch (ParserException e) {
			throw e;
		}
		catch (Exception e) {
			throw new ParserException("Could not parse file", e);
		}
	}
}
