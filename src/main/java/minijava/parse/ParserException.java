package minijava.parse;

import minijava.CompilerException;

public class ParserException extends CompilerException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7423131138945014762L;

	public ParserException(String message) {
		super(message);
	}
	
	public ParserException(String message, Throwable cause) {
		super(message, cause);
	}
}
