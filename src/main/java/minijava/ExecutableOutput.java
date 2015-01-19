package minijava;

public class ExecutableOutput {

	public final int exitValue;
	public final String stdOutput;
	public final String errOutput;
	public final String errText;
	
	public ExecutableOutput(int exitValue, String stdOutput, String errOutput, String errText) {
		this.exitValue = exitValue;
		this.stdOutput = stdOutput;
		this.errOutput = errOutput;
		this.errText = errText;
	}
}
