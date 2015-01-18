package minijava;

public class Logger {
	
	private static boolean verbose = false;
	
	static void setVerbose(boolean verbose) {
		Logger.verbose = verbose;
	}
	
	private static void printDelimiter() {
		System.out.println("-------------------------");
	}
	
	public static void logVerbose(String... messages) {
		if (verbose) {
			printDelimiter();
			for (int i = 0; i < messages.length; i++) {
				if (messages[i] != null) {
					if (i != 0) {
						printDelimiter();
					}
					System.out.println(messages[i]);
				}
			}
			printDelimiter();
		}
	}
}
