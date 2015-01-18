package minijava.registerallocation;

import minijava.CompilerException;

public class RegisterAllocatorException extends CompilerException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3398348609052961693L;

	public RegisterAllocatorException(String message) {
		super(message);
	}

	public RegisterAllocatorException(String message, Throwable cause) {
		super(message, cause);
	}
}
