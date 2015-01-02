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
	
	// Command line Arguments
	
	@Argument(usage = "Input files", required = true)
	private List<String> files = new ArrayList<String>();
	
	@Option(name = "--verbose", usage = "Print additional information")
	private boolean verbose;
	
	@Option(name = "--print-source-code", usage = "Pretty print the input source code", depends = "--verbose")
	private boolean printSourceCode;

	private void printDelimiter() {
		System.out.println("-------------------------");
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
			
			if (verbose) {
				printDelimiter();
				System.out.println("Successfully Parsed Input File");
				
				if (printSourceCode) {
					printDelimiter();
					System.out.print(program.accept(new PrettyPrintVisitor("")));
				}
				
				printDelimiter();
			}
			
			return program;
		}
		catch (IOException e) {
			throw new CompilerException("Lexer/Parser failed", e);
		}
	}
	
	public Program inferTypes(Prg program) throws CompilerException {
		
		try {
			SymbolTableVisitor symbolTableVisitor = new SymbolTableVisitor();
			Program symbolTable = program.accept(symbolTableVisitor);
			
			if (verbose) {
				printDelimiter();
				System.out.println("Successfully Built Symbol Table");
				printDelimiter();
			}
			
			return symbolTable;
		}
		catch (Exception e) {
			throw new CompilerException("Failed to create symbol table", e);
		}
	}
	
	public void checkTypes(Prg program, Program symbolTable) throws CompilerException {

		TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor(symbolTable);
		if (program.accept(typeCheckVisitor)) {
			
			if (verbose) {
				printDelimiter();
				System.out.println("Successfully Checked Types");
				printDelimiter();
			}
		} else {
			// TODO: Proper exceptions
			throw new CompilerException("Type Check Failed", new Exception(""));
		}
	}

	public static void main(String[] args) {
		// TODO code application logic here
		// SymbolTable table = new SymbolTable();

		MiniJavaCompiler compiler = new MiniJavaCompiler();
		
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
				
	
				MachineSpecifics machineSpecifics = new I386MachineSpecifics();//new DummyMachineSpecifics();
				IntermediateVisitor intermediateVisitor = new IntermediateVisitor(machineSpecifics, symbolTable);
				List<FragmentProc<TreeStm>> procFragements = program.accept(intermediateVisitor);
	
				String intermediateOutput;
	
				//try {
					List<FragmentProc<List<TreeStm>>> fragmentsCanonicalized = new ArrayList<>(procFragements.size());
					for (FragmentProc<TreeStm> fragment : procFragements) {
						FragmentProc<List<TreeStm>> canonFrag = (FragmentProc<List<TreeStm>>) fragment.accept(new Canon());
						Generator.BaseBlockContainer baseBlocks = Generator.generate(canonFrag.body);
						List<BaseBlock> tracedBaseBlocks = Tracer.trace(baseBlocks);
						List<TreeStm> tracedBody = ToTreeStmConverter.convert(tracedBaseBlocks, baseBlocks.startLabel, baseBlocks.endLabel);
	
						fragmentsCanonicalized.add(new FragmentProc<List<TreeStm>>(canonFrag.frame, tracedBody));
					}
	
					/*List<Fragment<TreeStm>> tempProcFragements = new LinkedList<>();
					for (FragmentProc<List<TreeStm>> frag : fragmentsCanonicalized) {
						tempProcFragements.add(new FragmentProc<TreeStm>(
							frag.frame,
						 	TreeStmSEQ.fromList(frag.body))
						 );
					}
	
					intermediateOutput = IntermediateToCmm.stmFragmentsToCmm(tempProcFragements);
					System.out.println(intermediateOutput);*/
	
					List<Fragment<List<Assem>>> assemFragments = new LinkedList<>();
					for (FragmentProc<List<TreeStm>> fragment : fragmentsCanonicalized) {
						assemFragments.add(machineSpecifics.codeGen(fragment));
					}
	
					intermediateOutput = machineSpecifics.printAssembly(assemFragments);
					System.out.println(intermediateOutput);
					
					System.out.println("-------------------------");
					
					// TODO: Build liveness graph
					for (Fragment<List<Assem>> frag : assemFragments) {
						SimpleGraph<Assem> controlFlowGraph = ControlFlowGraphBuilder.buildControlFlowGraph((FragmentProc<List<Assem>>) frag);
						String graphString = controlFlowGraph.getDot();
						
						System.out.println("******************");
						System.out.println(((FragmentProc<List<Assem>>)frag).frame.getName());
						System.out.println("******************");
						
						ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", "-c", "graph-easy --as boxart");
						
						processBuilder.directory(RUNTIME_DIRECTORY.toFile());
						processBuilder.environment().put("PATH", "/usr/local/bin:" + processBuilder.environment().get("PATH"));
						Process dotCall = processBuilder.start();
						OutputStream stdin = dotCall.getOutputStream();
						stdin.write(graphString.getBytes());
						stdin.close();
	
						try {
							dotCall.waitFor();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						
						InputStream stdout = dotCall.getInputStream();
						BufferedReader bufferedStderr = new BufferedReader(new InputStreamReader(stdout));
						String line;
						while ((line = bufferedStderr.readLine()) != null) {
							System.out.println(line);
						}
						bufferedStderr.close();
						stdout.close();
						
						InputStream stderr = dotCall.getErrorStream();
						bufferedStderr = new BufferedReader(new InputStreamReader(stderr));
						while ((line = bufferedStderr.readLine()) != null) {
							System.err.println(line);
							System.err.flush();
						}
						bufferedStderr.close();
						stderr.close();
						
						System.out.println();
					}
				/*}
				catch (Exception e) {
					intermediateOutput = IntermediateToCmm.stmFragmentsToCmm(procFragements);
					System.err.println(intermediateOutput);
					e.printStackTrace();
	
				}*/
	
				System.out.println("-------------------------");
	
				Runtime runtime = Runtime.getRuntime();
				// -xc specifies the input language as C and is required for GCC to read from stdin
				ProcessBuilder processBuilder = new ProcessBuilder(gcc, "-o", compilerOutputFile.toString(), "-m32", "-xc", "runtime_32.c", "-m32", "-xassembler", "-");
				processBuilder.directory(RUNTIME_DIRECTORY.toFile());
				Process gccCall = processBuilder.start();
				// Write C code to stdin of C Compiler
				OutputStream stdin = gccCall.getOutputStream();
				stdin.write(intermediateOutput.getBytes());
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