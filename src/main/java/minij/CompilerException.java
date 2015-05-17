package minij;

public class CompilerException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 819352314221235648L;

	public CompilerException(String message) {
		super(message);
	}

	public CompilerException(String message, Throwable cause) {
		super(message, cause);
	}
}