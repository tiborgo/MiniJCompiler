package minij.parse;

import minij.CompilerException;

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
