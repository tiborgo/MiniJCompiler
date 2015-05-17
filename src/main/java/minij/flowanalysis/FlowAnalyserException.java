package minij.flowanalysis;

import minij.CompilerException;

public class FlowAnalyserException extends CompilerException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1726779150293074577L;

	public FlowAnalyserException(String message) {
		super(message);
	}

	public FlowAnalyserException(String message, Throwable cause) {
		super(message, cause);
	}
}
