/*
 * MiniJCompiler: Compiler for a subset of the Java(R) language
 *
 * (C) Copyright 2014-2015 Tibor Goldschwendt <goldschwendt@cip.ifi.lmu.de>,
 * Michael Seifert <mseifert@error-reports.org>
 *
 * This file is part of MiniJCompiler.
 *
 * MiniJCompiler is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MiniJCompiler is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MiniJCompiler.  If not, see <http://www.gnu.org/licenses/>.
 */
package minij;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;

import minij.Configuration;
import minij.MiniJCompiler;
import minij.backend.i386.I386MachineSpecifics;

import org.apache.commons.io.FilenameUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class MiniJCompilerTest {

	@Parameterized.Parameters
	public static Collection<Object[]> files() throws IOException {
		return TestFiles.getFiles();
	}

	private File file;
	private Class<? extends Exception> exceptionClass;
	private MiniJCompiler compiler;
	
	public MiniJCompilerTest(File file, Class<? extends Exception> exceptionClass) {
		this.file = file;
		this.exceptionClass = exceptionClass;
	}
	
	@Before
	public void setUp() {
		compiler = new MiniJCompiler(new I386MachineSpecifics());
	}
	
	@Test
	public void testCompileExamples() throws IOException {

		System.out.println("Testing compiler input from file \"" + file.toString() + "\"");
		
		Configuration config =  new Configuration(new String[]{file.toString()});
		config.outputFile = "out" + File.separator + config.outputFile;

		try {
			compiler.compile(config);
			String output = compiler.runExecutable(config, 15);
			if (exceptionClass != null) {
				fail("The example " + file.toString() + " should have failed with exception " + exceptionClass + ", but was accepted by the compiler.");
			}
			
			byte[] content = Files.readAllBytes(new File("src/test/resources/minij-examples-outputs/working/" + FilenameUtils.getBaseName(file.toString()) + ".txt").toPath());
			String expectedOutput = new String(content);
			
			if (!output.equals(expectedOutput)) {
				fail("The example " + file.toString() + " should have printed '" + expectedOutput + "' but printed '" + output + "'");
			}
		}
		catch (Exception e) {
			
			if (exceptionClass == null) {
				e.printStackTrace();
				fail("The example " + file.toString() + " should have been accepted by the compiler but failed: " + e.getMessage());
			}
			
			if (!exceptionClass.isInstance(e)) {
				fail("The example " + file.toString() + " should have failed with exception " + exceptionClass + " but with failed with exception " + e.getClass() + ", " + e.getMessage());
			}
		}
	}
}
