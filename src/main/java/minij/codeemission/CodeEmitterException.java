package minij.codeemission;

import minij.CompilerException;

public class CodeEmitterException extends CompilerException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -960321490432226085L;

	public CodeEmitterException(String message) {
		super(message);
	}

	public CodeEmitterException(String message, Throwable cause) {
		super(message, cause);
	}
}
