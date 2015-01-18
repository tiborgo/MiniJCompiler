package minijava;

public class Logger {
	
	private static String step = "";
	private static boolean printedStep = false;
	private static boolean verbose = false;
	
	static void setVerbose(boolean verbose) {
		Logger.verbose = verbose;
	}
	
	private static void printDelimiter() {
		System.out.println("-----------------------------------");
	}
	
	private static void printDelimiter2() {
		System.out.println("***********************************");
	}
	
	private static void printStep() {
		printDelimiter2();
		System.out.println(step.toUpperCase());
		printDelimiter();
		printedStep = true;
	}
	
	public static void setStepName(String name) {
		step = name;
		printedStep = false;
	}
	
	public static void log(String... messages) {
		
		if (!printedStep) {
			printStep();
		}
		
		for (int i = 0; i < messages.length; i++) {
			if (messages[i] != null) {
				System.out.println(messages[i]);
			}
		}
	}
	
	public static void logVerbosely(String... messages) {
		if (verbose) {
			log(messages);
		}
	}
}
