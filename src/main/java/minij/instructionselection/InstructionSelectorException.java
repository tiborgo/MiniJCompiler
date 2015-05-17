package minij.instructionselection;

import minij.CompilerException;

public class InstructionSelectorException extends CompilerException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2696510738695405192L;

	public InstructionSelectorException(String message) {
		super(message);
	}

	public InstructionSelectorException(String message, Throwable cause) {
		super(message, cause);
	}
}
