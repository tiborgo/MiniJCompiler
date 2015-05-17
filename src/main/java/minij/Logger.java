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

public class Logger {
	
	private static String step = "";
	private static boolean printedStep = false;
	private static boolean verbose = false;
	
	static void setVerbose(boolean verbose) {
		Logger.verbose = verbose;
	}
	
	private static void printDelimiter() {
		System.out.println("-----------------------------------");
	}
	
	private static void printDelimiter2() {
		System.out.println("***********************************");
	}
	
	private static void printStep() {
		printDelimiter2();
		System.out.println(step.toUpperCase());
		printDelimiter();
		printedStep = true;
	}
	
	public static void setStepName(String name) {
		step = name;
		printedStep = false;
	}
	
	public static void log(String... messages) {
		
		if (!printedStep) {
			printStep();
		}
		
		for (int i = 0; i < messages.length; i++) {
			if (messages[i] != null) {
				System.out.println(messages[i]);
			}
		}
	}
	
	public static void logVerbosely(String... messages) {
		if (verbose) {
			log(messages);
		}
	}
}
