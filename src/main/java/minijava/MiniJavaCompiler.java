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

public class MiniJavaCompiler {
	public static final Path RUNTIME_DIRECTORY = Paths.get("src/main/resources/minijava/runtime");
	
	private final MachineSpecifics machineSpecifics;

	public MiniJavaCompiler(MachineSpecifics machineSpecifics) {
		this.machineSpecifics = machineSpecifics;
		
		String osName = System.getProperty("os.name").toLowerCase();
		// TODO: better solution?
		if (osName.contains("mac")) {
			Label.leadingUnderscore = true;
		}
		else {
			Label.leadingUnderscore = false;
		}
	}
	
	private void pipeline(Configuration config) throws CompilerException {
		Logger.setStepName("parse");
		Program program = Parser.parse(config);
		if (config.parse) return;
		
		Logger.setStepName("semantic analysis");
		Program typedProgram = SemanticAnalyser.analyseSemantics(config, program);
		if (config.semanticAnalysis) return;
		
		Logger.setStepName("translate");
		List<FragmentProc<TreeStm>> intermediate = Translator.translate(config, typedProgram, machineSpecifics);
		if (config.translate) return;
		
		Logger.setStepName("canonicalize");
		List<FragmentProc<List<TreeStm>>> intermediateCanonicalized = Canonicalizer.canonicalize(config, intermediate);
		if (config.canonicalize) return;
		
		Logger.setStepName("instruction selection");
		List<Fragment<List<Assem>>> assemFragments =  InstructionSelector.selectInstructions(config, intermediateCanonicalized, machineSpecifics);
		if (config.instructionSelection) return;
		
		Logger.setStepName("register allocation");
		List<Fragment<List<Assem>>> allocatedFragments = RegisterAllocator.allocateRegisters(config, assemFragments, machineSpecifics);
		if (config.registerAllocation) return;
		
		Logger.setStepName("code emission");
		String assembly = CodeEmitter.emitCode(config, allocatedFragments, machineSpecifics);
		if (config.codeEmission) return;
		
		Logger.setStepName("assembler");
		Assembler.assemble(config, assembly);
	}
	
	public float compile(Configuration config) throws CompilerException {
		
		try {
			Date startTime = new Date();
			pipeline(config);
			Date endTime = new Date();
			
			float interval = (endTime.getTime()-startTime.getTime())/1000f;
			
			if (!config.silent) {
				System.out.printf("Successfully generated " + config.outputFile + " in %.1f seconds%n", interval);
			}
			
			return interval;
		}
		catch(CompilerException e) {
			if (config.debug) {
				e.printStackTrace();
			}
			else if (!config.silent){
				System.err.println("Failed to compile input file: " + e.getMessage());
			}
			
			throw e;
		}
	}

	public String runExecutable(Configuration config, int timeOut_s) throws RunException, RunOutputException {

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

			StringBuilder stdOutput = new StringBuilder();
			StringBuilder errOutput = new StringBuilder();
			
			// read standard output
			InputStream stdout = outProcess.getInputStream();
			BufferedReader bufferedStdout = new BufferedReader(new InputStreamReader(stdout));
			while ((line = bufferedStdout.readLine()) != null) {
				stdOutput.append(line + System.lineSeparator());
			}
			bufferedStdout.close();
			stdout.close();
			
			// read error output
			InputStream stderr = outProcess.getErrorStream();
			BufferedReader bufferedStderr = new BufferedReader(new InputStreamReader(stderr));
			while ((line = bufferedStderr.readLine()) != null) {
				errOutput.append(line + System.lineSeparator());
			}
			bufferedStderr.close();
			stderr.close();
			
			String errText;

			switch (outProcess.exitValue()) {
			case 0:
				return stdOutput.toString(); 
			case 138:
				errText = "Bus error: 10";
				break;
			case 139:
				errText = "Segmentation Fault";
				break;
			default:
				errText = "Unknown error: " + outProcess.exitValue();
				break;
				
			}
			
			throw new RunOutputException(
				errText,
				outProcess.exitValue(),
				stdOutput.toString(),
				errOutput.toString()
			);
		}
		catch (IOException e) {
			throw new RunException("Failed to read output of compiled executable", e);
		}
	}

	public static void main (String[] args) {
		
		try{
			Configuration config = new Configuration(args);
			
			MiniJavaCompiler compiler = new MiniJavaCompiler(new I386MachineSpecifics());
			
			
			try {
				compiler.compile(config);
			}
			catch(CompilerException e) {
				System.exit(-1);
			}
			
			if (config.runExecutable) {
				try {
					String out = compiler.runExecutable(config, 0);
					if (!config.silent) {
						System.out.println(out);
					}
				}
				catch (RunException e) {
					
					if (config.debug) {
						e.printStackTrace();
					}
					else {
						System.err.println(e.getMessage());
					}
					System.exit(-1);
				}
				catch (RunOutputException e) {
					
					if (!config.silent) {
						System.out.println(e.getStdOutput());
						System.err.println(e.getErrOutput());
					}
					
					if (config.debug) {
						e.printStackTrace();
					}
					else {
						System.err.println(e.getMessage());
					}
					System.exit(-1);
				}
			}
		}
		catch (IllegalArgumentException e) {
			System.exit(-1);
		}
	}
}