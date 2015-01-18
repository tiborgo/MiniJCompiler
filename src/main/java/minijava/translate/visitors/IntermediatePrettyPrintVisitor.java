package minijava.translate.visitors;

import java.util.ArrayList;
import java.util.List;

import minijava.translate.tree.TreeExp;
import minijava.translate.tree.TreeExpCALL;
import minijava.translate.tree.TreeExpCONST;
import minijava.translate.tree.TreeExpESEQ;
import minijava.translate.tree.TreeExpMEM;
import minijava.translate.tree.TreeExpNAME;
import minijava.translate.tree.TreeExpOP;
import minijava.translate.tree.TreeExpTEMP;
import minijava.translate.tree.TreeStm;
import minijava.translate.tree.TreeStmCJUMP;
import minijava.translate.tree.TreeStmEXP;
import minijava.translate.tree.TreeStmJUMP;
import minijava.translate.tree.TreeStmLABEL;
import minijava.translate.tree.TreeStmMOVE;
import minijava.translate.tree.TreeStmSEQ;

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
