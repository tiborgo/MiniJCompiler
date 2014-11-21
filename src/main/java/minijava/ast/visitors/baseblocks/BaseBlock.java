package minijava.ast.visitors.baseblocks;

import java.util.List;

import minijava.intermediate.Label;
import minijava.intermediate.tree.TreeStm;
import minijava.intermediate.tree.TreeStmCJUMP;
import minijava.intermediate.tree.TreeStmJUMP;
import minijava.intermediate.tree.TreeStmLABEL;

public class BaseBlock {
	public final Label         label;
	public final TreeStm       jump;
	public final List<TreeStm> body;
	
	public BaseBlock(List<TreeStm> body) {
		
		if (body.size() < 2 &&
				!(body.get(0) instanceof TreeStmLABEL) &&
				!(body.get(this.body.size()-1) instanceof TreeStmCJUMP ||
						body.get(this.body.size()-1) instanceof TreeStmJUMP)) {
			throw new IllegalArgumentException("Base block's body has to start with label and end with jump");
		}
		
		this.body = body;
		this.label = ((TreeStmLABEL)body.get(0)).label;
		this.jump = body.get(body.size()-1);
	}
}
