package minijava.semanticanalysis;

import java.util.ArrayList;

import minijava.Configuration;
import minijava.Logger;
import minijava.parse.rules.Program;
import minijava.semanticanalysis.visitors.TypeCheckVisitor;
import minijava.semanticanalysis.visitors.TypeInferenceVisitor;

public class SemanticAnalyser {

	public static Program analyseSemantics(Configuration config, Program program) throws SemanticAnalyserException {
		
		Program typedProgram = new Program(new ArrayList<>(program.getClasses()));
		
		// infer types
		try {
			program.accept(new TypeInferenceVisitor());
		}
		catch (Exception e) {
			throw new SemanticAnalyserException("Failed to create symbol table", e);
		}
		
		Logger.logVerbosely("Successfully built symbol table");
		
		// check types
		try {
			if (program.accept(new TypeCheckVisitor())) {
	
			}
			else {
				throw new Exception("Type check failed");
			}
		}
		catch (Exception e) {
			throw new SemanticAnalyserException("Type check failed", e);
		}
		
		Logger.logVerbosely("Successfully checked types");
		
		return typedProgram;	
	}
}
