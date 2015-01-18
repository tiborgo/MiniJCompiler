package minijava;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import minijava.ast.rules.Program;
import minijava.ast.visitors.PrettyPrintVisitor;
import minijava.ast.visitors.TypeCheckVisitor;
import minijava.ast.visitors.TypeInferenceVisitor;
import minijava.ast.visitors.baseblocks.BaseBlock;
import minijava.ast.visitors.baseblocks.Generator;
import minijava.ast.visitors.baseblocks.ToTreeStmConverter;
import minijava.ast.visitors.baseblocks.Tracer;
import minijava.backend.Assem;
import minijava.backend.MachineSpecifics;
import minijava.backend.i386.I386MachineSpecifics;
import minijava.backend.livenessanalysis.ControlFlowGraphBuilder;
import minijava.backend.registerallocation.Allocator;
import minijava.intermediate.Fragment;
import minijava.intermediate.FragmentProc;
import minijava.intermediate.Label;
import minijava.intermediate.canon.Canon;
import minijava.intermediate.tree.TreeStm;
import minijava.intermediate.visitors.IntermediatePrettyPrintVisitor;
import minijava.intermediate.visitors.IntermediateVisitor;
import minijava.parsing_actions.ASTVisitor;
import minijava.util.SimpleGraph;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

//import java.nio.file.Files;

public class MiniJavaCompiler {
	private static final Path RUNTIME_DIRECTORY = Paths.get("src/main/resources/minijava/runtime");
	private final MachineSpecifics machineSpecifics;

	public static class RunException extends Exception {
		/**
		 *
		 */
		private static final long serialVersionUID = -7579391088215934802L;

		public RunException(String message) {
			super(message);
		}

		public RunException(String message, Throwable cause) {
			super(message, cause);
		}
	}

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
	
	private void printDelimiter() {
		System.out.println("-------------------------");
	}

	private void printVerbose(String... infos) {
		if (Configuration.getInstance().verbose) {
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

	private Program parse() throws CompilerException {
		try {
			ANTLRFileStream reader = new ANTLRFileStream(Configuration.getInstance().inputFile);
			MiniJavaLexer lexer = new MiniJavaLexer((CharStream) reader);
			TokenStream tokens = new CommonTokenStream(lexer);
			MiniJavaParser parser = new MiniJavaParser(tokens);
			ParseTree parseTree = parser.prog();
			ASTVisitor astVisitor = new ASTVisitor();
			Program program = (Program) astVisitor.visit(parseTree);

			printVerbose("Successfully parsed input file",
					(Configuration.getInstance().printSourceCode) ? program.accept(new PrettyPrintVisitor("")) : null);
			
			return program;
		}
		catch (IOException e) {
			throw new CompilerException("Lexer/parser failed", e);
		}
	}

	private Program inferTypes(Program program) throws CompilerException {

		try {
			TypeInferenceVisitor typeInferenceVisitor = new TypeInferenceVisitor();
			program.accept(typeInferenceVisitor);

			printVerbose("Successfully built symbol table");

			return program;
		}
		catch (Exception e) {
			// TODO: Proper exceptions
			throw new CompilerException("Failed to create symbol table", e);
		}
	}

	private void checkTypes(Program program) throws CompilerException {

		TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
		if (program.accept(typeCheckVisitor)) {

			printVerbose("Successfully checked types");

		} else {
			// TODO: Proper exceptions
			throw new CompilerException("Type check failed");
		}
	}

	private List<FragmentProc<TreeStm>> generateIntermediate(Program program) throws CompilerException {

		try {
			IntermediateVisitor intermediateVisitor = new IntermediateVisitor(machineSpecifics, program);
			List<FragmentProc<TreeStm>> procFragements = program.accept(intermediateVisitor);

			if (Configuration.getInstance().verbose) {
				String output = "";
				for (FragmentProc<TreeStm> frag : procFragements) {
					output += frag.body.accept(new IntermediatePrettyPrintVisitor()) + System.lineSeparator() + "-----" + System.lineSeparator();
				}
				System.out.println(output);
			}

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

				if (Configuration.getInstance().verbose) {
					String output = "*******" + System.lineSeparator();
					for (TreeStm stm : canonFrag.body) {
						output += stm.accept(new IntermediatePrettyPrintVisitor()) + System.lineSeparator() + "-----" + System.lineSeparator();
					}
					System.out.println(output);
				}

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

	private List<Fragment<List<Assem>>> generatePreAssembly (List<FragmentProc<List<TreeStm>>> intermediateCanonicalized) throws CompilerException {

		try {
			List<Fragment<List<Assem>>> assemFragments = new LinkedList<>();
			for (FragmentProc<List<TreeStm>> fragment : intermediateCanonicalized) {
				assemFragments.add(machineSpecifics.codeGen(fragment));
			}

			String assembly = null;
			if (Configuration.getInstance().printPreAssembly) {
				assembly = machineSpecifics.printAssembly(assemFragments);
			}

			printVerbose("Successfully generated assembly", assembly);

			return assemFragments;
		}
		catch (Exception e) {
			// TODO: proper exception
			throw new CompilerException("Failed to generate assembly", e);
		}
	}

	private List<SimpleGraph<Assem>> generateControlFlowGraphs (List<Fragment<List<Assem>>> assemFragments) throws CompilerException {

		try {
			List<SimpleGraph<Assem>> controlFlowGraphs = new ArrayList<>(assemFragments.size());
			for (Fragment<List<Assem>> frag : assemFragments) {
				controlFlowGraphs.add(ControlFlowGraphBuilder.build((FragmentProc<List<Assem>>) frag));
			}

			String graphOutput = null;
			if (Configuration.getInstance().printControlFlowGraphs) {
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

	/*private List<SimpleGraph<Temp>> generateInterferenceGraphs(List<SimpleGraph<Assem>> controlFlowGraphs) throws CompilerException {

		try {
			List<SimpleGraph<Temp>> interferenceGraphs = new LinkedList<>();
			for (SimpleGraph<Assem> controlFlowGraph : controlFlowGraphs) {
				Map<Assem, LivenessSetsBuilder.InOut> inOut = LivenessSetsBuilder.build(controlFlowGraph);


				Iterator<Assem> iter = inOut.keySet().iterator();
				StringBuilder inOutStringBuilder = new StringBuilder();
				inOutStringBuilder.append("[" + System.lineSeparator());
				while (iter.hasNext()) {
					Assem next = iter.next();
					inOutStringBuilder
						.append("\t   in: ")
						.append(inOut.get(next).in)
						.append(System.lineSeparator())
						.append("\t")
						.append(next)
						.append(System.lineSeparator())
						.append("\t   out:")
						.append(inOut.get(next).out);
					inOutStringBuilder
						.append(System.lineSeparator())
						.append(System.lineSeparator());
				}
				inOutStringBuilder.append("]");
				System.out.println(inOutStringBuilder);

				SimpleGraph<Temp> interferenceGraph = InterferenceGraphBuilder.build(controlFlowGraph, inOut);
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
	}*/

	private List<Fragment<List<Assem>>> allocateRegisters(List<Fragment<List<Assem>>> frags) throws CompilerException {

		try {
			List<Fragment<List<Assem>>> allocatedFrags = new LinkedList<>();
			for (int i = 0; i < frags.size(); i++) {
			//for (SimpleGraph<Temp> interferenceGraph : interferenceGraphs) {
				//SimpleGraph<Temp> interferenceGraph = interferenceGraphs.get(i);
				try {
					FragmentProc<List<Assem>> frag = (FragmentProc<List<Assem>>)frags.get(i);
					allocatedFrags.add(Allocator.allocate(frag, machineSpecifics));
				}
				catch (ClassCastException e) {
					throw new CompilerException("Can only alocate registers for FragementProc");
				}
			}

			/*String graphOutput = null;
			if (printPreColoredGraphs) {
				graphOutput = simpleGraphsToString(colroedInterferenceGraphs);
			}*/

			printVerbose("Successfully allocated registers");

			return allocatedFrags;

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

			graphOutput.append(dotCode);

			/*try {
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
			}*/
		}

		return graphOutput.toString();
	}

	private String generateAssembly (List<Fragment<List<Assem>>> assemFragments) throws CompilerException {
		try {

			String assembly = machineSpecifics.printAssembly(assemFragments);;
			
			printVerbose("Successfully generated assembly", (Configuration.getInstance().printAssembly) ? assembly : null);
			
			return assembly;
		}
		catch (Exception e) {
			// TODO: proper exception
			throw new CompilerException("Failed to generate assembly", e);
		}
	}
	
	private void compileAssembly (String assembly) throws CompilerException {
		
		try {
			// -xc specifies the input language as C and is required for GCC to read from stdin
			ProcessBuilder processBuilder = new ProcessBuilder("gcc", "-o", Configuration.getInstance().outputFile, "-m32", "-xc", "runtime_32.c", "-m32", "-xassembler", "-");
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

	public int runExecutable(int timeOut_s) throws RunException {

		try {
			final ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", "-c", "./" + Configuration.getInstance().outputFile);
			processBuilder.directory(RUNTIME_DIRECTORY.toFile());
			
			final Process outProcess = processBuilder.start();
			
			Runnable outCall = new Runnable() {

				@Override
				public void run() {
					try {
						outProcess.waitFor();
					}
					catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
				}
			};
			
			
			ExecutorService service = Executors.newSingleThreadExecutor();
			try {
			    Future<?> ft = service.submit(outCall);
			    try {
			    	if (timeOut_s != 0) {
			    		ft.get(timeOut_s, TimeUnit.SECONDS);
			    	}
			    	else {
			    		ft.get();
			    	}
			    }
			    catch (TimeoutException e) {
			    	outProcess.destroy();
			    	throw new RunException("Process did not return after " + timeOut_s + " seconds", e);
			    }
			    catch (ExecutionException | CancellationException | InterruptedException e) {
			    	throw new RunException("Process execution failed.", e);
			    }
			}
			finally {
			    service.shutdown();
			}

			

			String line;

			StringBuilder output = new StringBuilder();
			StringBuilder errOutput = new StringBuilder();

			switch (outProcess.exitValue()) {
			case 0:
				InputStream stdout = outProcess.getInputStream();
				BufferedReader bufferedStdout = new BufferedReader(new InputStreamReader(stdout));
				while ((line = bufferedStdout.readLine()) != null) {
					output.append(line + System.lineSeparator());
				}
				bufferedStdout.close();
				stdout.close();
				break;
			case 138:
				errOutput
					.append("Bus error: 10")
					.append(System.lineSeparator());
				break;
			case 139:
				errOutput
					.append("Segmentation Fault")
					.append(System.lineSeparator());
				break;
			default:
				System.err.println("Exit Code: " + outProcess.exitValue());
				InputStream stderr = outProcess.getErrorStream();
				BufferedReader bufferedStderr = new BufferedReader(new InputStreamReader(stderr));
				while ((line = bufferedStderr.readLine()) != null) {
					errOutput.append(line + System.lineSeparator());
				}
				bufferedStderr.close();
				stderr.close();
			}

			if (outProcess.exitValue() != 0) {
				System.err.println("Failed to run executable: " + errOutput.toString());
			}
			else {
				System.out.println(output.toString());
			}
			
			return outProcess.exitValue();

		}
		catch (IOException e) {
			throw new RunException("Failed to read output of compiled executable", e);
		}
	}
	
	public void compile() throws CompilerException {
		Program program = parse();
		Program symbolTable = inferTypes(program);
		checkTypes(program);
		List<FragmentProc<TreeStm>> intermediate = generateIntermediate(program);
		List<FragmentProc<List<TreeStm>>> intermediateCanonicalized = canonicalize(intermediate);
		List<Fragment<List<Assem>>> assemFragments = generatePreAssembly(intermediateCanonicalized);
		//List<SimpleGraph<Assem>> controlFlowGraphs = generateControlFlowGraphs(assemFragments);
		//List<SimpleGraph<Temp>> inferenceGraphs = generateInterferenceGraphs(controlFlowGraphs);

		List<Fragment<List<Assem>>> allocatedFrags = allocateRegisters(assemFragments);

		String assembly = generateAssembly(allocatedFrags);
		
		compileAssembly(assembly);
	}



	public static void main (String[] args) {
		
		if (Configuration.initialize(args)) {

			String osName = System.getProperty("os.name").toLowerCase();
			// TODO: better solution?
			if (osName.contains("mac")) {
				Label.leadingUnderscore = true;
			}
			else {
				Label.leadingUnderscore = false;
			}
			
			MiniJavaCompiler compiler = new MiniJavaCompiler(new I386MachineSpecifics());
			
			
			try {
				Date startTime = new Date();
				compiler.compile();
				Date endTime = new Date();
				System.out.printf("Successfully compiled input file in %.1f seconds%n", (endTime.getTime()-startTime.getTime())/1000f);
			}
			catch(CompilerException e) {
				e.printStackTrace();
				System.exit(-1);
			}
			
			if (Configuration.getInstance().runExecutable) {
				try {
					compiler.runExecutable(0);
				} catch (RunException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.exit(-1);
				}
			}
	    }
	}
}