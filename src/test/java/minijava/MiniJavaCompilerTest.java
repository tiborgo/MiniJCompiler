package minijava;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import minijava.backend.i386.I386MachineSpecifics;
import minijava.translate.layout.Label;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class MiniJavaCompilerTest {

	@Parameterized.Parameters
	public static Collection<Object[]> files() throws IOException {
		
		List<Object[]> files = new ArrayList<>();
		files.addAll(TestFiles.getWorkingFiles(TestFiles.EXAMPLE_PROGRAM_PATH_WORKING, TestFiles.EXAMPLE_PROGRAM_PATH_RUNTIME_FAILING));
		files.addAll(TestFiles.getFailingFiles(TestFiles.EXAMPLE_PROGRAM_PATH_PARSE_FAILING, TestFiles.EXAMPLE_PROGRAM_PATH_TYPE_FAILING));
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
	protected MiniJavaCompiler compiler;
	
	public MiniJavaCompilerTest(File file, boolean works) {
		this.file = file;
		this.works = works;
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
			if (!works) {
				fail("The example " + file.toString() + " should have failed, but was accepted by the compiler.");
			}
		}
		catch (Exception e) {
			if (works) {
				fail("The example " + file.toString() + " should have been accepted by the compiler but failed: " + e.getMessage());
			}
		}
	}
}
