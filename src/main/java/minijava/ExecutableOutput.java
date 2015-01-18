package minijava;

public class ExecutableOutput {

	public final int exitValue;
	public final String output;
	
	public ExecutableOutput(int exitValue, String output) {
		this.exitValue = exitValue;
		this.output = output;
	}
}
