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
package minij.backend.i386;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import minij.translate.layout.Frame;
import minij.translate.layout.Label;
import minij.translate.layout.Temp;
import minij.translate.tree.TreeExp;
import minij.translate.tree.TreeExpCONST;
import minij.translate.tree.TreeExpMEM;
import minij.translate.tree.TreeExpOP;
import minij.translate.tree.TreeExpTEMP;
import minij.translate.tree.TreeStm;
import minij.translate.tree.TreeStmMOVE;
import minij.translate.tree.TreeStmSEQ;
import minij.translate.tree.TreeExpOP.Op;

final class I386Frame implements Frame {

	final Label name;
	final List<Temp> params;
	final List<TreeExp> locals;
	int memoryLocalsOffset;

	I386Frame(I386Frame frame) {
		this.name = frame.name;
		this.params = new ArrayList<Temp>(frame.params);
		this.locals = new ArrayList<TreeExp>(frame.locals);
		memoryLocalsOffset = frame.memoryLocalsOffset;
	}

	I386Frame(Label name, int paramCount) {
		this.name = name;
		this.params = new ArrayList<Temp>();
		this.locals = new LinkedList<TreeExp>();
		for (int i = 0; i < paramCount; i++) {
			this.params.add(new Temp());
		}
		this.memoryLocalsOffset = I386MachineSpecifics.WORD_SIZE;
	}

	@Override
	public Label getName() {
		return name;
	}

	@Override
	public int getParameterCount() {
		return params.size();
	}

	@Override
	public TreeExp getParameter(int number) {
		Temp t = params.get(number);
		return (t == null) ? null : new TreeExpTEMP(t);
	}

	@Override
	public TreeExp addLocal(Location l) {
		TreeExp local;
		if (l == Location.ANYWHERE) {
			local = new TreeExpTEMP(new Temp());
		} else if (l == Location.IN_MEMORY) {
			local = new TreeExpMEM(
				new TreeExpOP(
					Op.MINUS,
					new TreeExpTEMP(I386MachineSpecifics.EBP.reg),
					new TreeExpCONST(memoryLocalsOffset)
				)
			);
			memoryLocalsOffset += I386MachineSpecifics.WORD_SIZE;
		} else {
			throw new IllegalArgumentException("Location must have value " + Location.ANYWHERE + " or " + Location.IN_MEMORY);
		}
		locals.add(local);
		return local;
	}

	@Override
	public TreeStm makeProc(TreeStm body, TreeExp returnValue) {
		return new TreeStmSEQ(body, new TreeStmMOVE(new TreeExpTEMP(
				I386MachineSpecifics.EAX.reg), returnValue));
	}

	@Override
	public Frame clone() {
		return new I386Frame(this);
	}

	@Override
	public int size() {
		return memoryLocalsOffset;
	}
}
