package minijava.intermediate.visitors;

import java.util.ArrayList;
import java.util.List;

import minijava.intermediate.tree.TreeExp;
import minijava.intermediate.tree.TreeStm;
import minijava.intermediate.tree.TreeExpCALL;
import minijava.intermediate.tree.TreeExpCONST;
import minijava.intermediate.tree.TreeExpESEQ;
import minijava.intermediate.tree.TreeExpMEM;
import minijava.intermediate.tree.TreeExpNAME;
import minijava.intermediate.tree.TreeExpOP;
import minijava.intermediate.tree.TreeExpTEMP;
import minijava.intermediate.tree.TreeStmCJUMP;
import minijava.intermediate.tree.TreeStmEXP;
import minijava.intermediate.tree.TreeStmJUMP;
import minijava.intermediate.tree.TreeStmLABEL;
import minijava.intermediate.tree.TreeStmMOVE;
import minijava.intermediate.tree.TreeStmSEQ;

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
		
		if (level < 10) {
			for (int i = 0; i < args.length; i++) {
				
				String arg;
				if (args[i] instanceof String) {
					arg = (String) args[i];
				}
				else if (args[i] instanceof TreeExp ||
						args[i] instanceof TreeStm) {
					
					increaseIndent();
					if (args[i] instanceof TreeExp) {
						arg = ((TreeExp)args[i]).accept(this);
					}
					else {
						arg = ((TreeStm)args[i]).accept(this);
					}
					decreaseIndent();
				}
				else {
					throw new IllegalArgumentException("'args' item must be of type String, TreeExp or TreeStm");
				}
				
				encloseBuilder.append(arg);
			
				if (i < args.length-1) {
					encloseBuilder
						.append(", ")
						.append(System.lineSeparator());
				}
			}
		}
		else {
			encloseBuilder
				.append("...")
				.append(System.lineSeparator());
		}
		
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
