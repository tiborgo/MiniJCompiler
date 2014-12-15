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
import minijava.backend.dummymachine.IntermediateToCmm;
import minijava.backend.i386.I386MachineSpecifics;
import minijava.intermediate.Fragment;
import minijava.intermediate.FragmentProc;
import minijava.intermediate.Label;
import minijava.intermediate.canon.Canon;
import minijava.intermediate.tree.TreeStm;
import minijava.intermediate.tree.TreeStmSEQ;
import minijava.symboltable.tree.Program;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class MiniJavaCompiler implements Frontend {
	private static final Path RUNTIME_DIRECTORY = Paths.get("src/main/resources/minijava/runtime");

	@Override
	public Prg getAbstractSyntaxTree(String filePath) throws IOException {
		ANTLRFileStream reader = new ANTLRFileStream(filePath);
		MiniJavaLexer lexer = new MiniJavaLexer((CharStream) reader);
		TokenStream tokens = new CommonTokenStream(lexer);
		MiniJavaParser parser = new MiniJavaParser(tokens);
		ParseTree parseTree = parser.prog();
		ASTVisitor astVisitor = new ASTVisitor();
		Prg program = (Prg) astVisitor.visit(parseTree);
		return program;
	}

	public static void main(String[] args) {
		// TODO code application logic here
		// SymbolTable table = new SymbolTable();

		String osName = System.getProperty("os.name").toLowerCase();
		// TODO: better solution?
		String gcc;
		if (osName.contains("mac")) {
			Label.leadingUnderscore = true;
			gcc = "/usr/local/bin/gcc-4.9";
		}
		else {
			gcc = "gcc";
		}

		MiniJavaCompiler compiler = new MiniJavaCompiler();
		Path compilerOutputFile = null;
		try {
			compilerOutputFile = Files.createTempFile("miniJavaCompiler", "CC.out");
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			Prg program = compiler.getAbstractSyntaxTree("src/test/resources/minijava-examples/working/MiniExample.java");
			PrettyPrintVisitor prettyPrintVisitor = new PrettyPrintVisitor("");
			String output = program.accept(prettyPrintVisitor);
			System.out.print(output);

			System.out.println("-------------------------");

			SymbolTableVisitor symbolTableVisitor = new SymbolTableVisitor();
			Program symbolTable = program.accept(symbolTableVisitor);

			TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor(symbolTable);
			if (program.accept(typeCheckVisitor)) {
				System.out.println("No Type Errors");
				System.out.flush();
			} else {
				System.err.println("Type errors");
				System.err.flush();
			}

			System.out.println("-------------------------");

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

				// TODO: Build liveness graph
				
				intermediateOutput = machineSpecifics.printAssembly(assemFragments);
				System.out.println(intermediateOutput);
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

		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Not Accepted");
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		} finally {
			try {
				Files.delete(compilerOutputFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}