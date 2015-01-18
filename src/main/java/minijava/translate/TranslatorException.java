package minijava.translate;

import minijava.CompilerException;

public class TranslatorException extends CompilerException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2636166084719665113L;

	public TranslatorException(String message) {
		super(message);
	}

	public TranslatorException(String message, Throwable cause) {
		super(message, cause);
	}
}
