package minijava;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;

import minijava.antlr.visitors.ASTVisitor;
import minijava.ast.rules.Prg;
import minijava.ast.visitors.IntermediateVisitor;
import minijava.ast.visitors.PrettyPrintVisitor;
import minijava.ast.visitors.SymbolTableVisitor;
import minijava.ast.visitors.TypeCheckVisitor;
import minijava.backend.MachineSpecifics;
import minijava.backend.dummymachine.DummyMachineSpecifics;
import minijava.backend.dummymachine.IntermediateToCmm;
import minijava.intermediate.Fragment;
import minijava.intermediate.tree.TreeStm;
import minijava.symboltable.tree.Program;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class MiniJavaCompiler implements Frontend {

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

		MiniJavaCompiler compiler = new MiniJavaCompiler();
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
			
			MachineSpecifics machineSpecifics = new DummyMachineSpecifics();
			IntermediateVisitor intermediateVisitor = new IntermediateVisitor(machineSpecifics, symbolTable);
			List<Fragment<TreeStm>> procFragements = program.accept(intermediateVisitor);
			
			String intermediateOutput = IntermediateToCmm.stmFragmentsToCmm(procFragements);
			System.out.println(intermediateOutput);
			
			System.out.println("-------------------------");
			
			Runtime runtime = Runtime.getRuntime();
			// -xc specifies the input language as C and is required for GCC to read from stdin
			Process gccCall = runtime.exec("gcc -Wno-int-to-pointer-cast -xc runtime.c -");
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
			
			Process outCall = runtime.exec("./a.out");
			
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
		}
	}
}