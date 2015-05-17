package minij;

public class RunOutputException extends CompilerException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 562964463423096417L;
	
	private final int exitValue;
	private final String stdOutput;
	private final String errOutput;
	private final String errText;

	public RunOutputException(
			String errText,
			int exitValue,
			String stdOutput,
			String errOutput) {
		
		super(errText + ": " + exitValue);
		this.exitValue = exitValue;
		this.stdOutput = stdOutput;
		this.errOutput = errOutput;
		this.errText = errText;
	}

	public RunOutputException(
			String errText,
			Throwable cause,
			int exitValue,
			String stdOutput,
			String errOutput) {
		
		super(errText + ": " + exitValue, cause);
		this.exitValue = exitValue;
		this.stdOutput = stdOutput;
		this.errOutput = errOutput;
		this.errText = errText;
	}
	
	public int getExitValue() {
		return exitValue;
	}

	public String getStdOutput() {
		return stdOutput;
	}

	public String getErrOutput() {
		return errOutput;
	}

	public String getErrText() {
		return errText;
	}
}
