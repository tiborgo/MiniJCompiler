package minij;

public class RunException extends Exception {
	/**
	 *
	 */
	private static final long serialVersionUID = -7579391088215934802L;

	public RunException(String message) {
		super(message);
	}

	public RunException(String message, Throwable cause) {
		super(message, cause);
	}
}