package minijava;

public class CompilerException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public CompilerException(String message) {
		super(message);
	}

	public CompilerException(String message, Throwable cause) {
		super(message, cause);
	}
}