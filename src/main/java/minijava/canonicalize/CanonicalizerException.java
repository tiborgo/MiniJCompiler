package minijava.canonicalize;

import minijava.CompilerException;

public class CanonicalizerException extends CompilerException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3446453032247650620L;

	public CanonicalizerException(String message) {
		super(message);
	}

	public CanonicalizerException(String message, Throwable cause) {
		super(message, cause);
	}
}
