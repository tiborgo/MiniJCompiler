package minij;

import org.apache.commons.io.FilenameUtils;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public class Configuration {

	// Command line Arguments
	
	@Argument(usage = "Input file", required = true)
	public String inputFile;
	
	@Option(name = "--output", aliases = {"-o"}, usage = "Output file")
	public String outputFile;
	
	@Option(name = "--verbose", aliases = {"-v"}, usage = "Print additional information")
	public boolean verbose;
	
	@Option(name = "--silent", aliases = {"-s"}, usage = "Print no output at all")
	public boolean silent;
	
	@Option(name = "--debug", aliases = {"-d"}, usage = "Print information in case of exceptions")
	public boolean debug;
	
	@Option(name = "--no-coalesce", aliases = {"-nc"}, usage = "Skips coalescing optimisation (slows down register allocation)")
	public boolean noCoalesce;
	
	@Option(name = "--run-executable", aliases = {"-e"}, usage = "Runs the compiled executable",
			forbids = {"--parse", "--semantic-analysis", "--translate", "--canonicalize", "--instruction-selection", "--register-allocation", "--code-emission"})
	public boolean runExecutable;
	
	// steps of the compilation pipeline
	@Option(name = "--parse", aliases = {"-p"},
			forbids = {"--semantic-analysis", "--translate", "--canonicalize", "--instruction-selection", "--register-allocation", "--code-emission", "--assembler"},
			usage = "compile until parse step")
	public boolean parse;
	
	@Option(name = "--semantic-analysis", aliases = {"-sa"},
			forbids = {"--parse", "--translate", "--canonicalize", "--instruction-selection", "--register-allocation", "--code-emission", "--assembler"},
			usage = "compile until semantic analysis step")
	public boolean semanticAnalysis;
	
	@Option(name = "--translate", aliases = {"-t"},
			forbids = {"--parse", "--semantic-analysis", "--canonicalize", "--instruction-selection", "--register-allocation", "--code-emission", "--assembler"},
			usage = "compile until translate step")
	public boolean translate;
	
	@Option(name = "--canonicalize", aliases = {"-c"},
			forbids = {"--parse", "--semantic-analysis", "--translate", "--instruction-selection", "--register-allocation", "--code-emission", "--assembler"},
			usage = "compile until canonicalize step")
	public boolean canonicalize;
	
	@Option(name = "--instruction-selection", aliases = {"-se"},
			forbids = {"--parse", "--semantic-analysis", "--translate", "--canonicalize", "--register-allocation", "--code-emission", "--assembler"},
			usage = "compile until instruction selection step")
	public boolean instructionSelection;
	
	@Option(name = "--register-allocation", aliases = {"-re"},
			forbids = {"--parse", "--semantic-analysis", "--translate", "--canonicalize", "--instruction-selection", "--code-emission", "--assembler"},
			usage = "compile until register allocation step")
	public boolean registerAllocation;
	
	@Option(name = "--code-emission", aliases = {"-ce"},
			forbids = {"--parse", "--semantic-analysis", "--translate", "--canonicalize", "--instruction-selection", "--register-allocation", "--assembler"},
			usage = "compile until code emission step")
	public boolean codeEmission;
	
	@Option(name = "--assembler", aliases = {"-a"},
			forbids = {"--parse", "--semantic-analysis", "--translate", "--canonicalize", "--instruction-selection", "--register-allocation", "--code-emission"},
			usage = "compile until assembler and linker step")
	public boolean assembler;
	
	@Option(name = "--print-source-code", aliases = {"-psc"}, usage = "Pretty print the input source code")
	public boolean printSourceCode;
	
	@Option(name = "--print-intermediate", aliases = {"-pi"}, usage = "Pretty print the intermediate code")
	public boolean printIntermediate;
	
	@Option(name = "--print-canonicalized-intermediate", aliases = {"-pci"}, usage = "Pretty print the canonicalized intermediate code")
	public boolean printCanonicalizedIntermediate;
	
	@Option(name = "--print-pre-assembly", aliases = {"-ppa"}, usage = "Prints the assembly with temporiaries and unspecified frame size")
	public boolean printPreAssembly;
	
	@Option(name = "--print-assembly", aliases = {"-pa"}, usage = "Prints the final assembly")
	public boolean printAssembly;
	
	@Option(name = "--print-control-flow-graphs", aliases = {"-pcfg"}, usage = "Prints the control flow graph")
	public boolean printControlFlowGraphs;
	
	@Option(name = "--print-interference-graphs", aliases = {"-pig"}, usage = "Prints the interference graphs")
	public boolean printInterferenceGraphs;
	
	@Option(name = "--print-pre-colored-graphs", aliases = {"-ppg"}, usage = "Prints the pre colored graphs")
	public boolean printPreColoredGraphs;
	
	@Option(name = "--print-register-allocation-details", aliases = {"-prad"}, usage = "Prints details of the register allocation")
	public boolean printRegisterAllocationDetails;
	
	@Option(name = "--print-coalescing-details", aliases = {"-pcd"}, usage = "Prints details of the coalescing algorithm")
	public boolean printCoalescingDetails;
	
	@Option(name = "--print-flow-analysis-details", aliases = {"-pfad"}, usage = "Prints details of the flow analysis")
	public boolean printFlowAnalysisDetails;
	
	public Configuration(String[] args) {
		
		CmdLineParser commandLineParser = new CmdLineParser(this);
		
		if (args.length == 0) {
	    	System.out.println("java MiniJCompiler [options...] input_file");
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
			
			if (outputFile == null) {
				if (codeEmission) {
					outputFile = FilenameUtils.getBaseName(inputFile) + ".s";
				}
				else {
					outputFile = FilenameUtils.getBaseName(inputFile) + ".out";
				}
			}
			
			Logger.setVerbose(verbose);
	    }
	}
}
