package minijava.assembler;

import minijava.CompilerException;

public class AssemblerException extends CompilerException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1567018737168949626L;

	public AssemblerException(String message) {
		super(message);
	}

	public AssemblerException(String message, Throwable cause) {
		super(message, cause);
	}
}
