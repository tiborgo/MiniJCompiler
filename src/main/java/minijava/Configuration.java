package minijava;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public class Configuration {

	// Command line Arguments
	
	@Argument(usage = "Input file", required = true)
	public String inputFile;
	
	@Option(name = "--output", usage = "Output file")
	public String outputFile = "CC.out";
	
	@Option(name = "--verbose", aliases = {"-v"}, usage = "Print additional information")
	public boolean verbose;
	
	@Option(name = "--print-source-code", aliases = {"-sc"}, usage = "Pretty print the input source code", depends = "--verbose")
	public boolean printSourceCode;
	
	@Option(name = "--print-pre-assembly", aliases = {"-pa"}, usage = "Prints the assembly with temporiaries and unspecified frame size", depends = "--verbose")
	public boolean printPreAssembly;
	
	@Option(name = "--print-assembly", aliases = {"-a"}, usage = "Prints the final assembly", depends = "--verbose")
	public boolean printAssembly;
	
	@Option(name = "--print-control-flow-graphs", aliases = {"-cfg"}, usage = "Prints the control flow graph", depends = "--verbose")
	public boolean printControlFlowGraphs;
	
	@Option(name = "--print-interference-graphs", aliases = {"-ig"}, usage = "Prints the interference graphs", depends = "--verbose")
	public boolean printInterferenceGraphs;
	
	@Option(name = "--run-executable", aliases = {"-re"}, usage = "Runs the compiled executable")
	public boolean runExecutable;
	
	@Option(name = "--print-pre-colored-graphs", aliases = {"-pg"},  depends = "--verbose")
	public boolean printPreColoredGraphs;
	
	private static Configuration instance;
	
	private Configuration() {
		
	}
	
	public static boolean initialize(String[] args) {
		
		instance = new Configuration();
		CmdLineParser commandLineParser = new CmdLineParser(instance);
		
		if (args.length == 0) {
	    	System.out.println("java MiniJavaCompiler [options...] input_file");
			System.out.println("Options:");
			commandLineParser.printUsage(System.out);
			instance = null;
			return false;
	    }
	    else {
			
			try {
				// Parse command line arguments
				commandLineParser.parseArgument(args);
			}
			catch(CmdLineException e) {
				System.err.println(e.getMessage());
				instance = null;
				return false;
			}
			
			return true;
	    }
	}
	
	public static Configuration getInstance() {
		if (instance != null) {
			return instance;
		}
		else {
			throw new IllegalStateException("Configuration has not been initialized");
		}
	}
}
