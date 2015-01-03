package minijava;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
//import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import minijava.antlr.visitors.ASTVisitor;
import minijava.ast.rules.Prg;
import minijava.ast.visitors.IntermediateVisitor;
import minijava.ast.visitors.PrettyPrintVisitor;
import minijava.ast.visitors.SymbolTableVisitor;
import minijava.ast.visitors.TypeCheckVisitor;
import minijava.ast.visitors.baseblocks.BaseBlock;
import minijava.ast.visitors.baseblocks.Generator;
import minijava.ast.visitors.baseblocks.ToTreeStmConverter;
import minijava.ast.visitors.baseblocks.Tracer;
import minijava.backend.Assem;
import minijava.backend.MachineSpecifics;
import minijava.backend.i386.I386MachineSpecifics;
import minijava.backend.livenessanalysis.ControlFlowGraphBuilder;
import minijava.backend.livenessanalysis.InterferenceGraphBuilder;
import minijava.backend.livenessanalysis.LivenessSetsBuilder;
import minijava.backend.livenessanalysis.ReverseOrderBuilder;
import minijava.backend.registerallocation.Allocator;
import minijava.backend.registerallocation.ColoredNode;
import minijava.intermediate.Fragment;
import minijava.intermediate.FragmentProc;
import minijava.intermediate.Label;
import minijava.intermediate.Temp;
import minijava.intermediate.canon.Canon;
import minijava.intermediate.tree.TreeStm;
import minijava.symboltable.tree.Program;
import minijava.util.Pair;
import minijava.util.SimpleGraph;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public class MiniJavaCompiler {
	private static final Path RUNTIME_DIRECTORY = Paths.get("src/main/resources/minijava/runtime");
	private final MachineSpecifics machineSpecifics;
	
	public static class CompilerException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		public CompilerException(String message) {
			super(message);
		}
		
		public CompilerException(String message, Throwable cause) {
			super(message, cause);
		}
	}
	
	public MiniJavaCompiler(MachineSpecifics machineSpecifics) {
		this.machineSpecifics = machineSpecifics;
	}
	
	// Command line Arguments
	
	@Argument(usage = "Input file", required = true)
	private String inputFile;
	
	@Option(name = "--output", usage = "Output file")
	private String outputFile = "CC.out";
	
	@Option(name = "--verbose", aliases = {"-v"}, usage = "Print additional information")
	private boolean verbose;
	
	@Option(name = "--print-source-code", usage = "Pretty print the input source code", depends = "--verbose")
	private boolean printSourceCode;
	
	@Option(name = "--print-assembly", aliases = {"-a"}, usage = "Prints the assembly", depends = "--verbose")
	private boolean printAssembly;
	
	@Option(name = "--print-control-flow-graphs", aliases = {"-cfg"}, usage = "Prints the control flow graph", depends = "--verbose")
	private boolean printControlFlowGraphs;
	
	@Option(name = "--print-interference-graphs", aliases = {"-ig"}, usage = "Prints the interference graphs", depends = "--verbose")
	private boolean printInterferenceGraphs;
	
	@Option(name = "--run-executable", usage = "Runs the compiled executable")
	private boolean runExecutable;
	
	@Option(name = "--skip-type-check", aliases = {"-st"}, usage = "Skips the type check")
	private boolean skipTypeCheck;
	
	@Option(name = "--print-pre-colored-graphs", aliases = {"-pg"},  depends = "--verbose")
	private boolean printPreColoredGraphs;
	
	private void printDelimiter() {
		System.out.println("-------------------------");
	}
	
	private void printVerbose(String... infos) {
		if (verbose) {
			printDelimiter();
			for (int i = 0; i < infos.length; i++) {
				if (infos[i] != null) {
					if (i != 0) {
						printDelimiter();
					}
					System.out.println(infos[i]);
				}
			}
			printDelimiter();
		}
	}

	// Compiler pipeline
	
	private Prg parse() throws CompilerException {
		try {
			ANTLRFileStream reader = new ANTLRFileStream(inputFile);
			MiniJavaLexer lexer = new MiniJavaLexer((CharStream) reader);
			TokenStream tokens = new CommonTokenStream(lexer);
			MiniJavaParser parser = new MiniJavaParser(tokens);
			ParseTree parseTree = parser.prog();
			ASTVisitor astVisitor = new ASTVisitor();
			Prg program = (Prg) astVisitor.visit(parseTree);
			
			printVerbose("Successfully parsed input file",
					(printSourceCode) ? program.accept(new PrettyPrintVisitor("")) : null);
			
			return program;
		}
		catch (IOException e) {
			throw new CompilerException("Lexer/parser failed", e);
		}
	}
	
	private Program inferTypes(Prg program) throws CompilerException {
		
		try {
			SymbolTableVisitor symbolTableVisitor = new SymbolTableVisitor();
			Program symbolTable = program.accept(symbolTableVisitor);
			
			printVerbose("Successfully built symbol table");
			
			return symbolTable;
		}
		catch (Exception e) {
			// TODO: Proper exceptions
			throw new CompilerException("Failed to create symbol table", e);
		}
	}
	
	private void checkTypes(Prg program, Program symbolTable) throws CompilerException {

		TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor(symbolTable);
		if (program.accept(typeCheckVisitor)) {
			
			printVerbose("Successfully checked types");

		} else {
			// TODO: Proper exceptions
			throw new CompilerException("Type check failed");
		}
	}
	
	private List<FragmentProc<TreeStm>> generateIntermediate(Prg program, Program symbolTable) throws CompilerException {
		
		try {
			IntermediateVisitor intermediateVisitor = new IntermediateVisitor(machineSpecifics, symbolTable);
			List<FragmentProc<TreeStm>> procFragements = program.accept(intermediateVisitor);
			
			printVerbose("Successfully generated intermediate language");
			
			return procFragements;
		}
		catch (Exception e) {
			// TODO: proper exceptions
			throw new CompilerException("Failed to generate intermediate language", e);
		}
	}
	
	private List<FragmentProc<List<TreeStm>>> canonicalize(List<FragmentProc<TreeStm>> intermediate) throws CompilerException {
		
		try {
			List<FragmentProc<List<TreeStm>>> intermediateCanonicalized = new ArrayList<>(intermediate.size());
			for (FragmentProc<TreeStm> fragment : intermediate) {
				FragmentProc<List<TreeStm>> canonFrag = (FragmentProc<List<TreeStm>>) fragment.accept(new Canon());
				Generator.BaseBlockContainer baseBlocks = Generator.generate(canonFrag.body);
				List<BaseBlock> tracedBaseBlocks = Tracer.trace(baseBlocks);
				List<TreeStm> tracedBody = ToTreeStmConverter.convert(tracedBaseBlocks, baseBlocks.startLabel, baseBlocks.endLabel);
	
				intermediateCanonicalized.add(new FragmentProc<List<TreeStm>>(canonFrag.frame, tracedBody));
			}
			
			printVerbose("Successfully canonicalized intermediate language");
			
			return intermediateCanonicalized;
		}
		catch (Exception e) {
			// TODO: proper exception
			throw new CompilerException("Failed to canonicalize intermediate language", e);
		}
	}
	
	private List<Fragment<List<Assem>>> generateAssembly (List<FragmentProc<List<TreeStm>>> intermediateCanonicalized) throws CompilerException {
		
		try {
			List<Fragment<List<Assem>>> assemFragments = new LinkedList<>();
			for (FragmentProc<List<TreeStm>> fragment : intermediateCanonicalized) {
				assemFragments.add(machineSpecifics.codeGen(fragment));
			}
			
			printVerbose("Successfully generated assembly");
			
			return assemFragments;
		}
		catch (Exception e) {
			// TODO: proper exception
			throw new CompilerException("Failed to generate assembly", e);
		}
	}
	
	private String generateAssemblyCode (List<Fragment<List<Assem>>> assemFragments) throws CompilerException {
		
		try {
			String assembly = machineSpecifics.printAssembly(assemFragments);
			
			printVerbose("Successfully generated assembly code",
					(printAssembly) ? assembly : null);
			
			return assembly;
		}
		catch (Exception e) {
			// TODO: proper exception
			throw new CompilerException("Failed to generate assembly code", e);
		}
	}

	private List<SimpleGraph<Assem>> generateControlFlowGraphs (List<Fragment<List<Assem>>> assemFragments) throws CompilerException {
		
		try {
			List<SimpleGraph<Assem>> controlFlowGraphs = new ArrayList<>(assemFragments.size());
			for (Fragment<List<Assem>> frag : assemFragments) {
				controlFlowGraphs.add(ControlFlowGraphBuilder.build((FragmentProc<List<Assem>>) frag));
			}
			
			String graphOutput = null;
			if (printControlFlowGraphs) {
				graphOutput = simpleGraphsToString(controlFlowGraphs);
			}
			
			printVerbose("Successfully generated control flow graphs", graphOutput);
			
			return controlFlowGraphs;
		}
		catch (Exception e) {
			// TODO: proper exception
			throw new CompilerException("Failed to generate control flow graph", e);
		}
	}
	
	private List<SimpleGraph<Temp>> generateInterferenceGraphs(List<SimpleGraph<Assem>> controlFlowGraphs) throws CompilerException {
		
		try {
			List<SimpleGraph<Temp>> interferenceGraphs = new LinkedList<>();
			for (SimpleGraph<Assem> controlFlowGraph : controlFlowGraphs) {
				Pair<Map<Assem, Set<Temp>>, Map<Assem, Set<Temp>>> livenessSets = LivenessSetsBuilder.build(controlFlowGraph);
				SimpleGraph<Temp> interferenceGraph = InterferenceGraphBuilder.build(controlFlowGraph, livenessSets.fst, livenessSets.snd);
				interferenceGraphs.add(interferenceGraph);
			}
			
			String graphOutput = null;
			if (printInterferenceGraphs) {
				graphOutput = simpleGraphsToString(interferenceGraphs);
			}
			
			printVerbose("Successfully generated interference graphs", graphOutput);
			
			return interferenceGraphs;
		}
		catch (Exception e) {
			// TODO: proper exception
			throw new CompilerException("Failed to generate interference graphs", e);
		}
	}
	
	private void allocateRegisters(List<SimpleGraph<Temp>> interferenceGraphs) throws CompilerException {
		
		try {
			List<SimpleGraph<ColoredNode>> colroedInterferenceGraphs = new LinkedList<>();
			for (SimpleGraph<Temp> interferenceGraph : interferenceGraphs) {
				colroedInterferenceGraphs.add(Allocator.allocate(interferenceGraph, machineSpecifics));
			}
			
			String graphOutput = null;
			if (printPreColoredGraphs) {
				graphOutput = simpleGraphsToString(colroedInterferenceGraphs);
			}
			
			printVerbose("Successfully generated interference graphs", graphOutput);

		}
		catch (Exception e) {
			// TODO: proper exception
			throw new CompilerException("Failed to generate interference graphs", e);
		}
		
	}
	
	private <T> String simpleGraphsToString (List<SimpleGraph<T>> graphs) throws CompilerException {
		
		StringBuilder graphOutput = new StringBuilder();
		
		for (SimpleGraph<T> graph : graphs) {
			String dotCode = graph.getDot();
			
			graphOutput.append(System.lineSeparator());
			graphOutput.append(graph.getName() + System.lineSeparator());
			graphOutput.append(System.lineSeparator());
			
			try {
				ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", "-c", "graph-easy --as boxart");
				processBuilder.environment().put("PATH", "/usr/local/bin:" + processBuilder.environment().get("PATH"));
				Process graphEasyCall = processBuilder.start();
				OutputStream stdin = graphEasyCall.getOutputStream();
				stdin.write(dotCode.getBytes());
				stdin.close();
	
				graphEasyCall.waitFor();
				
				InputStream stdout = graphEasyCall.getInputStream();
				BufferedReader bufferedStdout = new BufferedReader(new InputStreamReader(stdout));
				String line;
				while ((line = bufferedStdout.readLine()) != null) {
					graphOutput.append(line + System.lineSeparator());
				}
				bufferedStdout.close();
				stdout.close();
				
				if (graphEasyCall.exitValue() != 0) {
				
					StringBuilder errOutput = new StringBuilder();
					InputStream stderr = graphEasyCall.getErrorStream();
					BufferedReader bufferedStderr = new BufferedReader(new InputStreamReader(stderr));
					while ((line = bufferedStderr.readLine()) != null) {
						errOutput.append(line + System.lineSeparator());
					}
					bufferedStderr.close();
					stderr.close();

					throw new CompilerException("Failed to create print of control flow graph: " + errOutput.toString());
				}
			}
			catch (IOException e) {
				throw new CompilerException("Failed to transfer dot code to graph-easy", e);
			}
			catch (InterruptedException e) {
				throw new CompilerException("Failed to invoke graph-easy", e);
			}
		}
		
		return graphOutput.toString();
	}
	
	private void compileAssembly (String gcc, String assembly) throws CompilerException {
		
		try {
			// -xc specifies the input language as C and is required for GCC to read from stdin
			ProcessBuilder processBuilder = new ProcessBuilder(gcc, "-o", outputFile, "-m32", "-xc", "runtime_32.c", "-m32", "-xassembler", "-");
			processBuilder.directory(RUNTIME_DIRECTORY.toFile());
			Process gccCall = processBuilder.start();
			// Write C code to stdin of C Compiler
			OutputStream stdin = gccCall.getOutputStream();
			stdin.write(assembly.getBytes());
			stdin.close();
	
			gccCall.waitFor();
			
			// Print error messages of GCC
			if (gccCall.exitValue() != 0) {
				
				StringBuilder errOutput = new StringBuilder();
				InputStream stderr = gccCall.getErrorStream();
				String line;
				BufferedReader bufferedStderr = new BufferedReader(new InputStreamReader(stderr));
				while ((line = bufferedStderr.readLine()) != null) {
					errOutput.append(line + System.lineSeparator());
				}
				bufferedStderr.close();
				stderr.close();

				throw new CompilerException("Failed to compile assembly:" + System.lineSeparator() + errOutput.toString());
			}
			
			printVerbose("Successfully compiled assembly");
		}
		catch (IOException e) {
			throw new CompilerException("Failed to transfer assembly to gcc", e);
		}
		catch (InterruptedException e) {
			throw new CompilerException("Failed to invoke gcc", e);
		}
		
	}
	
	private void runExecutable() throws CompilerException {
		
		try {
			ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", "-c", "./" + outputFile);
			processBuilder.directory(RUNTIME_DIRECTORY.toFile());
			Process outCall = processBuilder.start();

			outCall.waitFor();

			String line;
			
			StringBuilder output = new StringBuilder();
			StringBuilder errOutput = new StringBuilder();
			
			switch (outCall.exitValue()) {
			case 0:
				InputStream stdout = outCall.getInputStream();
				BufferedReader bufferedStdout = new BufferedReader(new InputStreamReader(stdout));
				while ((line = bufferedStdout.readLine()) != null) {
					output.append(line + System.lineSeparator());
				}
				bufferedStdout.close();
				stdout.close();
				break;
			case 139:
				errOutput.append("Segmentation Fault" + System.lineSeparator());
				break;
			default:
				System.err.println("Exit Code: " + outCall.exitValue());
				InputStream stderr = outCall.getErrorStream();
				BufferedReader bufferedStderr = new BufferedReader(new InputStreamReader(stderr));
				while ((line = bufferedStderr.readLine()) != null) {
					errOutput.append(line + System.lineSeparator());
				}
				bufferedStderr.close();
				stderr.close();
			}
			
			if (outCall.exitValue() != 0) {
				throw new CompilerException("Failed to compile assembly:" + System.lineSeparator() + errOutput.toString());
			}
			else {
				System.out.println(output.toString());
			}

		}
		catch (IOException e) {
			throw new CompilerException("Failed to read output of compiled executable", e);
		}
		catch (InterruptedException e) {
			throw new CompilerException("Failed to invoke compiled executable", e);
		}
	}
	
	public void compile(String gcc) throws CompilerException {
		Prg program = parse();
		Program symbolTable = inferTypes(program);
		if (!skipTypeCheck) {
			checkTypes(program, symbolTable);
		}
		List<FragmentProc<TreeStm>> intermediate = generateIntermediate(program, symbolTable);
		List<FragmentProc<List<TreeStm>>> intermediateCanonicalized = canonicalize(intermediate); 
		List<Fragment<List<Assem>>> assemFragments = generateAssembly(intermediateCanonicalized);
		String assembly = generateAssemblyCode(assemFragments);
		List<SimpleGraph<Assem>> controlFlowGraphs = generateControlFlowGraphs(assemFragments);
		List<SimpleGraph<Temp>> inferenceGraphs = generateInterferenceGraphs(controlFlowGraphs);
		
		allocateRegisters(inferenceGraphs);
		
		// TODO Allocate registers
		
		compileAssembly(gcc, assembly);
		if (runExecutable) {
			runExecutable();
		}
	}

	

	public static void main (String[] args) {

		MiniJavaCompiler compiler = new MiniJavaCompiler(new I386MachineSpecifics());
		
	    CmdLineParser commandLineParser = new CmdLineParser(compiler);
	    
	    if (args.length == 0) {
	    	System.out.println("java MiniJavaCompiler [options...] input_file");
			System.out.println("Options:");
			commandLineParser.printUsage(System.out);
	    }
	    else {
			
			try {
				// Parse command line arguments
				commandLineParser.parseArgument(args);
			}
			catch(CmdLineException e) {
				System.err.println(e.getMessage());
				System.exit(-1);
			}
	
			
			String osName = System.getProperty("os.name").toLowerCase();
			// TODO: better solution?
			String gcc;
			if (osName.contains("mac")) {
				Label.leadingUnderscore = true;
				gcc = "/usr/local/bin/gcc-4.9";
			}
			else {
				Label.leadingUnderscore = false;
				gcc = "gcc";
			}
			
			try {
				compiler.compile(gcc);
			}
			catch(CompilerException e) {
				e.printStackTrace();
				System.exit(-1);
			}
	
			
			/*Path compilerOutputFile = null;
			try {
				compilerOutputFile = Files.createTempFile("miniJavaCompiler", "CC.out");
			} catch (IOException e) {
				e.printStackTrace();
			}*/
			
			
			
			/*finally {
				try {
					Files.delete(compilerOutputFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}*/
	    }
	}

	
}