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
	
	@Option(name = "--print-intermediate", aliases = {"-i"}, usage = "Pretty print the input source code", depends = "--verbose")
	public boolean printIntermediate;
	
	@Option(name = "--print-canonicalized-intermediate", aliases = {"-ci"}, usage = "Pretty print the input source code", depends = "--verbose")
	public boolean printCanonicalzedIntermediate;
	
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
	
	@Option(name = "--debug", aliases = {"-d"})
	public boolean debug;
	
	public Configuration(String[] args) {
		
		CmdLineParser commandLineParser = new CmdLineParser(this);
		
		if (args.length == 0) {
	    	System.out.println("java MiniJavaCompiler [options...] input_file");
			System.out.println("Options:");
			commandLineParser.printUsage(System.out);
			throw new IllegalArgumentException("Not enough arguments");
	    }
	    else {
			
			try {
				// Parse command line arguments
				commandLineParser.parseArgument(args);
			}
			catch(CmdLineException e) {
				System.err.println(e.getMessage());
				throw new IllegalArgumentException("Failed parsing arguments", e);
			}
			
			Logger.setVerbose(verbose);
	    }
	}
}
