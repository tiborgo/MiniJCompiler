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
package minij.translate.visitors;

import java.util.ArrayList;
import java.util.List;

import minij.translate.tree.TreeExp;
import minij.translate.tree.TreeExpCALL;
import minij.translate.tree.TreeExpCONST;
import minij.translate.tree.TreeExpESEQ;
import minij.translate.tree.TreeExpMEM;
import minij.translate.tree.TreeExpNAME;
import minij.translate.tree.TreeExpOP;
import minij.translate.tree.TreeExpTEMP;
import minij.translate.tree.TreeExpVisitor;
import minij.translate.tree.TreeStm;
import minij.translate.tree.TreeStmCJUMP;
import minij.translate.tree.TreeStmEXP;
import minij.translate.tree.TreeStmJUMP;
import minij.translate.tree.TreeStmLABEL;
import minij.translate.tree.TreeStmMOVE;
import minij.translate.tree.TreeStmSEQ;
import minij.translate.tree.TreeStmVisitor;

public class IntermediatePrettyPrintVisitor implements
	TreeExpVisitor<String, RuntimeException>,
	TreeStmVisitor<String, RuntimeException> {
	
	private String indent = "";
	private int level = 0;
	
	private void increaseIndent() {
		indent += "  ";
		level++;
	}
	
	private void decreaseIndent() {
		indent = indent.substring(0, indent.length()-2);
		level--;
	}
	
	private String enclose(String name, Object... args) {
		
		StringBuilder encloseBuilder = new StringBuilder();
		
		encloseBuilder
			.append(indent).append(name).append("(").append(System.lineSeparator());
		
		increaseIndent();
		
		if (level < 10) {
			for (int i = 0; i < args.length; i++) {

				if (args[i] instanceof String) {
					encloseBuilder
						.append(indent)
						.append(args[i]);
				}
				else if (args[i] instanceof TreeExp) {
					encloseBuilder.append(((TreeExp)args[i]).accept(this));
				}
				else if (args[i] instanceof TreeStm) {
					encloseBuilder.append(((TreeStm)args[i]).accept(this));
				}
				else {
					throw new IllegalArgumentException("'args' item must be of type String, TreeExp or TreeStm");
				}
			
				if (i < args.length-1) {
					encloseBuilder
						.append(", ")
						.append(System.lineSeparator());
				}
			}
		}
		else {
			encloseBuilder
				.append(indent)
				.append("...");
		}
		
		decreaseIndent();
		
		encloseBuilder
			.append(System.lineSeparator())
			.append(indent).append(")");
		
		return encloseBuilder.toString();
	}
	
	@Override
	public String visit(TreeStmMOVE s) {
		return enclose("MOVE", s.dest, s.src);
	}

	@Override
	public String visit(TreeStmEXP s) {
		return enclose("EXP", s.exp);
	}

	@Override
	public String visit(TreeStmJUMP s) {
		
		if (s.poss.size() > 1) {
			throw new UnsupportedOperationException("Cannot print jump to multiple possibilities");
		}
		
		return enclose("JUMP", s.dest);
	}

	@Override
	public String visit(TreeStmCJUMP s) {
		return enclose("CJUMP", s.rel.toString(), s.left, s.right, s.ltrue.toString(), s.lfalse.toString());
	}

	@Override
	public String visit(TreeStmSEQ s) {
		return enclose("SEQ", s.first, s.second);
	}

	@Override
	public String visit(TreeStmLABEL stmLABEL) {
		return indent + "LABEL(" + stmLABEL.label + ")";
	}

	@Override
	public String visit(TreeExpCALL e) throws RuntimeException {

		List<TreeExp> args = new ArrayList<>(e.args.size()+1);
		args.add(e.func);
		args.addAll(e.args);
		
		return enclose("CALL", args.toArray());
	}

	@Override
	public String visit(TreeExpCONST e) throws RuntimeException {
		return indent + "CONST(" + e.value + ")";
	}

	@Override
	public String visit(TreeExpESEQ e) throws RuntimeException {
		return enclose("ESEQ", e.stm, e.res);
	}

	@Override
	public String visit(TreeExpMEM e) throws RuntimeException {
		return enclose("MEM", e.addr);
	}

	@Override
	public String visit(TreeExpNAME e) throws RuntimeException {
		return indent + "NAME(" + e.label + ")";
	}

	@Override
	public String visit(TreeExpOP e) throws RuntimeException {
		return enclose("OP", e.op.toString(), e.left, e.right);
	}

	@Override
	public String visit(TreeExpTEMP e) throws RuntimeException {
		return indent + "TEMP(" + e.temp + ")";
	}

}
