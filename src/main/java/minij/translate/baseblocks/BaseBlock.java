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
package minij.translate.baseblocks;

import java.util.ArrayList;
import java.util.List;

import minij.translate.layout.Label;
import minij.translate.tree.TreeExpNAME;
import minij.translate.tree.TreeStm;
import minij.translate.tree.TreeStmCJUMP;
import minij.translate.tree.TreeStmJUMP;
import minij.translate.tree.TreeStmLABEL;

/**
 * Represents a snippet of low-level code that is used for the serialization of the call graph.
 * @see minij.translate.baseblocks.Tracer
 */
public class BaseBlock {
	/** Entry point of the code block. */
	public final Label         label;
	/** Statements to be executed. */
	public final List<TreeStm> body;
	/** Jump statement when exiting the block. */
	public final TreeStm       jump;

	public BaseBlock(List<TreeStm> body) {
		
		if (body.size() < 2) {
			throw new IllegalArgumentException("A BaseBlock must consist of at least two statements.");
		}
		if (!(body.get(0) instanceof TreeStmLABEL)) {
			throw new IllegalArgumentException("A BaseBlock must start with a label.");
		}
		if (!(body.get(body.size()-1) instanceof TreeStmCJUMP || body.get(body.size()-1) instanceof TreeStmJUMP)) {
			throw new IllegalArgumentException("A BaseBlock must wnd with a jump or conditional jump.");
		}
		
		this.body = new ArrayList<>(body);
		this.label = ((TreeStmLABEL)body.get(0)).label;
		this.jump = body.get(body.size()-1);
	}
	
	@Override
	public String toString() {
		return "{" + label + ",...," +
				((jump instanceof TreeStmJUMP) ?
						"(JUMP, " + ((TreeExpNAME)((TreeStmJUMP)jump).dest).label + ")" :
							"(CJUMP,..., " + ((TreeStmCJUMP)jump).ltrue + ", " + ((TreeStmCJUMP)jump).lfalse + ")") +
				"}";
	}
	
}
