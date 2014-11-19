package minijava.ast.visitors.baseblocks;

import java.util.List;

import minijava.intermediate.Label;
import minijava.intermediate.tree.TreeStm;

public class BaseBlock {
	public final Label         label;
	public final TreeStm       jump;
	public final List<TreeStm> body;
	
	public BaseBlock(Label label, TreeStm jump, List<TreeStm> body) {
		this.label = label;
		this.jump = jump;
		this.body = body;
	}
}
