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

public class RunOutputException extends CompilerException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 562964463423096417L;
	
	private final int exitValue;
	private final String stdOutput;
	private final String errOutput;
	private final String errText;

	public RunOutputException(
			String errText,
			int exitValue,
			String stdOutput,
			String errOutput) {
		
		super(errText + ": " + exitValue);
		this.exitValue = exitValue;
		this.stdOutput = stdOutput;
		this.errOutput = errOutput;
		this.errText = errText;
	}

	public RunOutputException(
			String errText,
			Throwable cause,
			int exitValue,
			String stdOutput,
			String errOutput) {
		
		super(errText + ": " + exitValue, cause);
		this.exitValue = exitValue;
		this.stdOutput = stdOutput;
		this.errOutput = errOutput;
		this.errText = errText;
	}
	
	public int getExitValue() {
		return exitValue;
	}

	public String getStdOutput() {
		return stdOutput;
	}

	public String getErrOutput() {
		return errOutput;
	}

	public String getErrText() {
		return errText;
	}
}
