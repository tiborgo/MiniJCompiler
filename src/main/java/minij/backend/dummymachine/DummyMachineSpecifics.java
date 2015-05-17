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
package minij.backend.dummymachine;

import java.util.List;

import minij.instructionselection.MachineSpecifics;
import minij.instructionselection.assems.Assem;
import minij.translate.layout.Fragment;
import minij.translate.layout.Frame;
import minij.translate.layout.Label;
import minij.translate.layout.Temp;
import minij.translate.tree.TreeStm;

/**
 * Dummy compiler target that does nothing.
 * All methods will raise an exception, except for {@link #getWordSize()} and {@link #newFrame(minij.translate.layout.Label, int)}.
 */
public class DummyMachineSpecifics implements MachineSpecifics {

	@Override
	public Frame newFrame(Label name, int params) {
		return new DummyMachineFrame(name, params);
	}

	@Override
	public int getWordSize() {
		return 4;
	}

	@Override
	public Temp[] getAllRegisters() {
		throw new UnsupportedOperationException("Registers allocation not supported.");
	}

	@Override
	public Temp[] getGeneralPurposeRegisters() {
		throw new UnsupportedOperationException("Register allocation not supported.");
	}

	@Override
	public String printAssembly(List<Fragment<List<Assem>>> frags) {
		throw new UnsupportedOperationException("Generic machine doesn't support assembly code!");
	}

	@Override
	public Fragment<List<Assem>> codeGen(Fragment<List<TreeStm>> frag) {
		throw new UnsupportedOperationException("Generic machine doesn't support assembly code!");
	}

	@Override
	public List<Assem> spill(Frame frame, List<Assem> instrs, List<Temp> toSpill) {
		throw new UnsupportedOperationException("Generic machine doesn't support assembly code!");
	}

}
