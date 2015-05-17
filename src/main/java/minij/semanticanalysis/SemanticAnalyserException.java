package minij.semanticanalysis;

import minij.CompilerException;

public class SemanticAnalyserException extends CompilerException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4146476901859127293L;

	public SemanticAnalyserException(String message) {
		super(message);
	}
	
	public SemanticAnalyserException(String message, Throwable cause) {
		super(message, cause);
	}
}
