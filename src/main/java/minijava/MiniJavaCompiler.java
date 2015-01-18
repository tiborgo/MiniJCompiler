package minijava;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import minijava.assembler.Assembler;
import minijava.backend.i386.I386MachineSpecifics;
import minijava.canonicalize.Canonicalizer;
import minijava.codeemission.CodeEmitter;
import minijava.instructionselection.InstructionSelector;
import minijava.instructionselection.MachineSpecifics;
import minijava.instructionselection.assems.Assem;
import minijava.parse.Parser;
import minijava.parse.rules.Program;
import minijava.registerallocation.RegisterAllocator;
import minijava.semanticanalysis.SemanticAnalyser;
import minijava.translate.Translator;
import minijava.translate.layout.Fragment;
import minijava.translate.layout.FragmentProc;
import minijava.translate.layout.Label;
import minijava.translate.tree.TreeStm;
import minijava.util.SimpleGraph;

//import java.nio.file.Files;

public class MiniJavaCompiler {
	public static final Path RUNTIME_DIRECTORY = Paths.get("src/main/resources/minijava/runtime");
	
	private final MachineSpecifics machineSpecifics;

	public MiniJavaCompiler(MachineSpecifics machineSpecifics) {
		this.machineSpecifics = machineSpecifics;
	}

	// Compiler pipeline

	

	

	

	

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
	
	public void compile(Configuration config) throws CompilerException {
		
		Program program = Parser.parse(config);
		Program typedProgram = SemanticAnalyser.analyseSemantics(config, program);
		List<FragmentProc<TreeStm>> intermediate = Translator.translate(config, typedProgram, machineSpecifics);
		List<FragmentProc<List<TreeStm>>> intermediateCanonicalized = Canonicalizer.canonicalize(config, intermediate);
		List<Fragment<List<Assem>>> assemFragments =  InstructionSelector.selectInstructions(config, intermediateCanonicalized, machineSpecifics);
		List<Fragment<List<Assem>>> allocatedFragments = RegisterAllocator.allocateRegisters(assemFragments, machineSpecifics);
		String assembly = CodeEmitter.emitCode(config, allocatedFragments, machineSpecifics);
		Assembler.assemble(config, assembly);
	}

	public int runExecutable(Configuration config, int timeOut_s) throws RunException {

		try {
			final ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", "-c", "./" + config.outputFile);
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

	public static void main (String[] args) {
		
		Configuration config;
		try{
			config = new Configuration(args);
		}
		catch (IllegalArgumentException e) {
			System.exit(-1);
			return;
		}


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
			compiler.compile(config);
			Date endTime = new Date();
			System.out.printf("Successfully compiled input file in %.1f seconds%n", (endTime.getTime()-startTime.getTime())/1000f);
		}
		catch(CompilerException e) {
			if (config.debug) {
				e.printStackTrace();
			}
			else {
				System.err.println("Failed to compile input file: " + e.getMessage());
			}
			System.exit(-1);
		}
		
		if (config.runExecutable) {
			try {
				compiler.runExecutable(config, 0);
			} catch (RunException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(-1);
			}
		}
	}
}