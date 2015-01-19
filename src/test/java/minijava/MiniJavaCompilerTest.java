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
		outputs.put("BinarySearch", "20\n21\n22\n23\n24\n25\n26\n27\n28\n29\n30\n31\n32\n33\n34\n35\n36\n37\n38\n99999\n0\n0\n1\n1\n1\n1\n0\n0\n999\n");
		outputs.put("BinaryTree", "16\n100000000\n8\n16\n4\n8\n12\n14\n16\n20\n24\n28\n1\n1\n1\n0\n1\n4\n8\n14\n16\n20\n24\n28\n0\n0\n");
		outputs.put("BubbleSort", "...");
		outputs.put("Effects", "0\n");
		outputs.put("Factorial", "3628800\n");
		outputs.put("Fib", "1973\n");
		outputs.put("FibL", "1973\n");
		outputs.put("GameOfLife", "...");
		outputs.put("Graph", "...");
		outputs.put("LinearSearch", "10\n11\n12\n13\n14\n15\n16\n17\n18\n9999\n0\n1\n1\n0\n55\n");
		outputs.put("LinkedList", "25\n10000000\n39\n25\n10000000\n22\n39\n25\n1\n0\n10000000\n28\n22\n39\n25\n2220000\n-555\n-555\n28\n22\n25\n33300000\n22\n25\n44440000\n0\n");
		outputs.put("ManyArgs", "1\n0\n2\n1\n3\n1\n4\n0\n5\n1\n10\n0\n89\n1\n999\n");
		outputs.put("Newton", "2\n999\n577\n408\n0\n");
		outputs.put("Precedence", "5\n");
		outputs.put("Primes", "2\n3\n5\n7\n11\n13\n17\n19\n999\n8\n");
		outputs.put("QuickSort", "...");
		outputs.put("Scope", "5\n");
		outputs.put("Scope2", "5\n");
		outputs.put("ShortCutAnd", "0\n");
		outputs.put("Stck", "55\n");
		outputs.put("Sum", "15\n");
		outputs.put("TestEq", "1\n0\n");
		outputs.put("TrivialClass", "555\n");
		outputs.put("While", "1\n3\n6\n10\n15\n21\n28\n36\n45\n55\n0\n");

		// BubbleSort probably wrong
		/*"20
		7
		12
		18
		2
		11
		6
		9
		19
		5
		99999
		20
		20
		20
		20
		20
		20
		20
		20
		20
		20
		0
		"*/
		
		/* Graph:
		'1
		4
		999
		4
		1
		-999
		3
		2
		999
		4
		1
		-999
		3
		2
		999
		4
		3
		-999
		5
		3
		-999
		5
		4        // should be 5
		-999     // should be 999
		0
		'
		*/
		
		/* Quicksort, probably wrong
		'20
		7
		12
		18
		2
		11
		6
		9
		19
		5
		9999
		20
		7
		12
		18
		20
		11
		6
		9
		19
		7
		0
		'
		*/
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
