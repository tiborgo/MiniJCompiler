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
		outputs.put("BubbleSort", "20\n7\n12\n18\n2\n11\n6\n9\n19\n5\n99999\n2\n5\n6\n7\n9\n11\n12\n18\n19\n20\n0\n");
		outputs.put("Effects", "0\n");
		outputs.put("Factorial", "3628800\n");
		outputs.put("Fib", "1973\n");
		outputs.put("FibL", "1973\n");
		outputs.put("GameOfLife", "64996\n71097\n76875\n87498\n88137\n84740\n96705\n91420\n116099\n79512\n91456\n93370\n105344\n103035\n110942\n80700\n84818\n89765\n102305\n101800\n108620\n107338\n96301\n115194\n84415\n98656\n93209\n94275\n98564\n99724\n84909\n91366\n97886\n109247\n109898\n106889\n119558\n115031\n139722\n103503\n116233\n118917\n130913\n129024\n137661\n108187\n112311\n117644\n130958\n131209\n138047\n137161\n126874\n146511\n115756\n130375\n125698\n127520\n131819\n133373\n119324\n126553\n133085\n144860\n146267\n144024\n156731\n152582\n169934\n126364\n146463\n149147\n161143\n159254\n167891\n138417\n142541\n147874\n161188\n161439\n168277\n167391\n157104\n176741\n145986\n160605\n155928\n157750\n176361\n142310\n106576\n91366\n97886\n109247\n109898\n106889\n119558\n115031\n139722\n103503\n116233\n118917\n130913\n129024\n137661\n108187\n112311\n117644\n130958\n131209\n138047\n137161\n126874\n146511\n115756\n130375\n125698\n127520\n131819\n133373\n119324\n126553\n133085\n144860\n146267\n144024\n156731\n152582\n169934\n126364\n146463\n149147\n161143\n159254\n167891\n138417\n142541\n147874\n161188\n161439\n168277\n167391\n157104\n176741\n145986\n160605\n155928\n157750\n176361\n142310\n106576\n91366\n97886\n109247\n109898\n106889\n119558\n115031\n139722\n103503\n116233\n118917\n130913\n129024\n137661\n108187\n112311\n117644\n130958\n131209\n138047\n137161\n126874\n146511\n115756\n130375\n125698\n127520\n131819\n133373\n119324\n126553\n133085\n144860\n146267\n144024\n156731\n152582\n169934\n126364\n146463\n149147\n161143\n159254\n167891\n138417\n142541\n147874\n161188\n161439\n999999999\n");
		outputs.put("Graph", "1\n4\n999\n4\n1\n-999\n3\n2\n999\n4\n1\n-999\n3\n2\n999\n4\n3\n-999\n5\n5\n999\n5\n4\n-999\n0\n");
		outputs.put("LinearSearch", "10\n11\n12\n13\n14\n15\n16\n17\n18\n9999\n0\n1\n1\n0\n55\n");
		outputs.put("LinkedList", "25\n10000000\n39\n25\n10000000\n22\n39\n25\n1\n0\n10000000\n28\n22\n39\n25\n2220000\n-555\n-555\n28\n22\n25\n33300000\n22\n25\n44440000\n0\n");
		outputs.put("ManyArgs", "1\n0\n2\n1\n3\n1\n4\n0\n5\n1\n10\n0\n89\n1\n999\n");
		outputs.put("Newton", "2\n999\n577\n408\n0\n");
		outputs.put("Precedence", "5\n");
		outputs.put("Primes", "2\n3\n5\n7\n11\n13\n17\n19\n999\n8\n");
		outputs.put("QuickSort", "20\n7\n12\n18\n2\n11\n6\n9\n19\n5\n9999\n2\n5\n6\n7\n9\n11\n12\n18\n19\n20\n0\n");
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
			String output = compiler.runExecutable(config, 15);
			if (exceptionClass != null) {
				fail("The example " + file.toString() + " should have failed with exception " + exceptionClass + ", but was accepted by the compiler.");
			}
			String expectedOutput = outputs.get(FilenameUtils.getBaseName(file.toString()));
			if (!output.equals(expectedOutput)) {
				fail("The example " + file.toString() + " should have printed '" + expectedOutput + "' but printed '" + output + "'");
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
