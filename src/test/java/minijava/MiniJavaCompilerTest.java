package minijava;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import minijava.backend.i386.I386MachineSpecifics;

import org.apache.commons.io.FilenameUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class MiniJavaCompilerTest {

	@Parameterized.Parameters
	public static Collection<Object[]> files() throws IOException {
		return TestFiles.getFiles();
	}

	private File file;
	private Class<? extends Exception> exceptionClass;
	private MiniJavaCompiler compiler;
	private static Map<String, String> outputs;
	
	@BeforeClass
	public static void setUpOutputs() {
		outputs = new HashMap<>();
		outputs.put("ArrayAccess", "5\n");
		outputs.put("ArrSum", "55\n");
		outputs.put("BinarySearch", "...");
		outputs.put("BinaryTree", "...");
		outputs.put("BubbleSort", "...");
		outputs.put("Effects", "0\n");
		outputs.put("Factorial", "3628800\n");
		outputs.put("Fib", "1973\n");
		outputs.put("FibL", "1973\n");
		outputs.put("GameOfLife", "...");
		outputs.put("Graph", "...");
		outputs.put("LinearSearch", "10\n11\n12\n13\n14\n15\n16\n17\n18\n9999\n0\n1\n1\n0\n55\n");
		outputs.put("LinkedList", "..."); // "10000000\n10000000\n0\n0\n10000000\n2220000\n33300000\n44440000\n0\n");
		outputs.put("ManyArgs", "1\n0\n2\n1\n3\n1\n4\n0\n5\n1\n10\n0\n89\n1\n999\n");
		outputs.put("Newton", "2\n999\n577\n408\n0\n");
		outputs.put("Precedence", "5\n");
		outputs.put("Primes", "...");
		outputs.put("QuickSort", "...");
		outputs.put("Scope", "5\n");
		outputs.put("Scope2", "5\n");
		outputs.put("ShortCutAnd", "0\n");
		outputs.put("Stck", "55\n");
		outputs.put("Sum", "15\n");
		outputs.put("TestEq", "1\n0\n");
		outputs.put("TrivialClass", "555\n");
		outputs.put("While", "1\n3\n6\n10\n15\n21\n28\n36\n45\n55\n0\n");
	}
	
	public MiniJavaCompilerTest(File file, Class<? extends Exception> exceptionClass) {
		this.file = file;
		this.exceptionClass = exceptionClass;
	}
	
	@Before
	public void setUp() {
		compiler = new MiniJavaCompiler(new I386MachineSpecifics());
	}
	
	@Test
	public void testCompileExamples() throws IOException {

		System.out.println("Testing compiler input from file \"" + file.toString() + "\"");
		
		Configuration config =  new Configuration(new String[]{file.toString()});

		try {
			compiler.compile(config);
			ExecutableOutput out = compiler.runExecutable(config, 15);
			if (exceptionClass != null) {
				fail("The example " + file.toString() + " should have failed with exception " + exceptionClass + ", but was accepted by the compiler.");
			}
			String expectedOutput = outputs.get(FilenameUtils.getBaseName(file.toString()));
			if (!out.output.equals(expectedOutput)) {
				fail("The example " + file.toString() + " should have printed '" + expectedOutput + "' but printed '" + out.output + "'");
			}
		}
		catch (Exception e) {
			
			if (exceptionClass == null) {
				fail("The example " + file.toString() + " should have been accepted by the compiler but failed: " + e.getMessage());
			}
			
			if (!exceptionClass.isInstance(e)) {
				fail("The example " + file.toString() + " should have failed with exception " + exceptionClass + " but with failed with exception " + e.getClass() + ", " + e.getMessage());
			}
		}
	}
}
