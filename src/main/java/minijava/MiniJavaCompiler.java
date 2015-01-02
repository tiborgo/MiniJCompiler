package minijava;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
import minijava.backend.controlflowanalysis.ControlFlowGraphBuilder;
import minijava.backend.i386.I386MachineSpecifics;
import minijava.intermediate.Fragment;
import minijava.intermediate.FragmentProc;
import minijava.intermediate.Label;
import minijava.intermediate.canon.Canon;
import minijava.intermediate.tree.TreeStm;
import minijava.symboltable.tree.Program;
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
		private final Exception nested;
		
		public CompilerException(String message, Exception nested) {
			super(message);
			this.nested = nested;
		}
		
		@Override
		public void printStackTrace() {
			super.printStackTrace();
			System.err.println("nested:");
			nested.printStackTrace();
		}
		
	}
	
	public MiniJavaCompiler(MachineSpecifics machineSpecifics) {
		this.machineSpecifics = machineSpecifics;
	}
	
	// Command line Arguments
	
	@Argument(usage = "Input files", required = true)
	private List<String> files = new ArrayList<String>();
	
	@Option(name = "--verbose", usage = "Print additional information")
	private boolean verbose;
	
	@Option(name = "--print-source-code", usage = "Pretty print the input source code", depends = "--verbose")
	private boolean printSourceCode;
	
	@Option(name = "--print-assembly", usage = "Prints the assembly", depends = "--verbose")
	private boolean printAssembly;
	
	@Option(name = "--print-control-flow-graphs", usage = "Prints the control flow graph", depends = "--verbose")
	private boolean printControlFlowGraphs;

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
	
	public Prg parse() throws CompilerException {
		try {
			ANTLRFileStream reader = new ANTLRFileStream(files.get(0));
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
	
	public Program inferTypes(Prg program) throws CompilerException {
		
		try {
			SymbolTableVisitor symbolTableVisitor = new SymbolTableVisitor();
			Program symbolTable = program.accept(symbolTableVisitor);
			
			printVerbose("Successfully built symbol table");
			
			return symbolTable;
		}
		catch (Exception e) {
			throw new CompilerException("Failed to create symbol table", e);
		}
	}
	
	public void checkTypes(Prg program, Program symbolTable) throws CompilerException {

		TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor(symbolTable);
		if (program.accept(typeCheckVisitor)) {
			
			printVerbose("Successfully checked types");

		} else {
			// TODO: Proper exceptions
			throw new CompilerException("Type check failed", new Exception(""));
		}
	}
	
	public List<FragmentProc<TreeStm>> generateIntermediate(Prg program, Program symbolTable) throws CompilerException {
		
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
				controlFlowGraphs.add(ControlFlowGraphBuilder.buildControlFlowGraph((FragmentProc<List<Assem>>) frag));
			}
			
			StringBuilder graphOutput = new StringBuilder();
			if (printControlFlowGraphs) {
				
				for (int i = 0; i < controlFlowGraphs.size(); i++) {
					String dotCode = controlFlowGraphs.get(i).getDot();
					
					graphOutput.append(System.lineSeparator());
					graphOutput.append(((FragmentProc<List<Assem>>)assemFragments.get(i)).frame.getName() + System.lineSeparator());
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
	
							throw new CompilerException("Failed to create print of control flow graph: " + errOutput.toString(), new Exception());
						}
					}
					catch (IOException e) {
						throw new CompilerException("Failed to transfer dot code to graph-easy", e);
					}
					catch (InterruptedException e) {
						throw new CompilerException("Failed to invoke graph-easy", e);
					}
				}
			}
			
			printVerbose("Successfully generated control flow graphs",
					(printControlFlowGraphs) ? graphOutput.toString() : null);
			
			return controlFlowGraphs;
		}
		catch (Exception e) {
			// TODO: proper exception
			throw new CompilerException("Failed to generate control flow graph", e);
		}
	}
	
	public static void main(String[] args) {
		// TODO code application logic here
		// SymbolTable table = new SymbolTable();

		MiniJavaCompiler compiler = new MiniJavaCompiler(new I386MachineSpecifics());
		
	    CmdLineParser commandLineParser = new CmdLineParser(compiler);
	    
	    if (args.length == 0) {
	    	System.out.println("java MiniJavaCompiler [options...] input files");
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
	
			
			Path compilerOutputFile = null;
			try {
				compilerOutputFile = Files.createTempFile("miniJavaCompiler", "CC.out");
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			try {
				Prg program = compiler.parse();
				Program symbolTable = compiler.inferTypes(program);
				compiler.checkTypes(program, symbolTable);
				List<FragmentProc<TreeStm>> intermediate = compiler.generateIntermediate(program, symbolTable);
				List<FragmentProc<List<TreeStm>>> intermediateCanonicalized = compiler.canonicalize(intermediate); 
				List<Fragment<List<Assem>>> assemFragments = compiler.generateAssembly(intermediateCanonicalized);
				String assembly = compiler.generateAssemblyCode(assemFragments);
				List<SimpleGraph<Assem>> controlFlowGraphs = compiler.generateControlFlowGraphs(assemFragments);
	
				System.out.println("-------------------------");
	
				Runtime runtime = Runtime.getRuntime();
				// -xc specifies the input language as C and is required for GCC to read from stdin
				ProcessBuilder processBuilder = new ProcessBuilder(gcc, "-o", compilerOutputFile.toString(), "-m32", "-xc", "runtime_32.c", "-m32", "-xassembler", "-");
				processBuilder.directory(RUNTIME_DIRECTORY.toFile());
				Process gccCall = processBuilder.start();
				// Write C code to stdin of C Compiler
				OutputStream stdin = gccCall.getOutputStream();
				stdin.write(assembly.getBytes());
				stdin.close();
	
				try {
					gccCall.waitFor();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
	
				// Print error messages of GCC
				InputStream stderr = gccCall.getErrorStream();
				BufferedReader bufferedStderr = new BufferedReader(new InputStreamReader(stderr));
				String line;
				while ((line = bufferedStderr.readLine()) != null) {
					System.err.println(line);
					System.err.flush();
				}
				bufferedStderr.close();
				stderr.close();
	
				int retVal = gccCall.exitValue();
				if (retVal == 0) {
					System.out.println("Successful GCC compilation");
				} else {
					System.err.println("GCC compilation failed");
					System.err.flush();
				}
	
				System.out.println("-------------------------");
	
				Process outCall = runtime.exec(compilerOutputFile.toString());
	
				try {
					outCall.waitFor();
	
					switch (outCall.exitValue()) {
					case 0:
						InputStream stdout = outCall.getInputStream();
						BufferedReader bufferedStdout = new BufferedReader(new InputStreamReader(stdout));
						while ((line = bufferedStdout.readLine()) != null) {
							System.out.println(line);
						}
						bufferedStdout.close();
						stdout.close();
						break;
					case 139:
						System.err.println("Segmentation Fault");
						System.err.flush();
						break;
					default:
						System.err.println("Exit Code: " + outCall.exitValue());
						stderr = outCall.getErrorStream();
						bufferedStderr = new BufferedReader(new InputStreamReader(stderr));
						while ((line = bufferedStderr.readLine()) != null) {
							System.err.println(line);
						}
						System.err.flush();
						bufferedStderr.close();
						stderr.close();
					}
	
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
	
			} catch (CompilerException e) {
				e.printStackTrace();
				System.out.println("Compilation failed");
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			finally {
				try {
					Files.delete(compilerOutputFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
	    }
	}

	
}