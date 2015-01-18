package minijava;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import minijava.backend.i386.I386MachineSpecifics;
import minijava.intermediate.Label;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class MiniJavaRunTest {

	@Parameterized.Parameters
	public static Collection<Object[]> files() throws IOException {
		
		List<Object[]> files = new ArrayList<>();
		files.addAll(TestFiles.getWorkingFiles(TestFiles.EXAMPLE_PROGRAM_PATH_WORKING));
		files.addAll(TestFiles.getFailingFiles(TestFiles.EXAMPLE_PROGRAM_PATH_RUNTIME_FAILING));
		return files;
	}

	@BeforeClass
	public static void setUpClass() {
		String osName = System.getProperty("os.name").toLowerCase();
		if (osName.contains("mac")) {
			Label.leadingUnderscore = true;
		} else {
			Label.leadingUnderscore = false;
		}
	}

	private File file;
	private boolean works;
	private MiniJavaCompiler compiler;
	
	public MiniJavaRunTest(File file, boolean works) {
		this.file = file;
		this.works = works;
	}
	
	@Before
	public void setUp() {
		compiler = new MiniJavaCompiler(new I386MachineSpecifics());
	}
	
	@Test
	public void testRunExamples() throws IOException, CompilerException, RunException {

		System.out.println("Testing compiler input from file \"" + file.toString() + "\"");
		
		Configuration.initialize(new String[]{file.toString()});
		
		compiler.compile(Configuration.getInstance());
		
		try {
			if(compiler.runExecutable(10) == 0) {
				if (!works) {
					fail("The example " + file.toString() + " should have failed, but ran successfully.");
				}
			}
			else {
				if (works) {
					fail("The example " + file.toString() + " should have run successfully but failed.");
				}
			}
		}
		catch (RunException e) {
			fail("The execution of example " + file.toString() + " failed: " + e.getMessage());
		}
	}
}
