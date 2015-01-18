package minijava.backend.i386;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import minijava.translate.Frame;
import minijava.translate.Label;
import minijava.translate.Temp;
import minijava.translate.tree.TreeExp;
import minijava.translate.tree.TreeExpCONST;
import minijava.translate.tree.TreeExpMEM;
import minijava.translate.tree.TreeExpOP;
import minijava.translate.tree.TreeExpTEMP;
import minijava.translate.tree.TreeStm;
import minijava.translate.tree.TreeStmMOVE;
import minijava.translate.tree.TreeStmSEQ;
import minijava.translate.tree.TreeExpOP.Op;

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
