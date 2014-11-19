package minijava.ast.visitors.baseblocks;

import java.util.LinkedList;
import java.util.List;

import minijava.intermediate.Label;
import minijava.intermediate.tree.TreeStm;
import minijava.intermediate.tree.TreeStmLABEL;

public class ToTreeStmConverter {

	private final Label endLabel;
	
	public ToTreeStmConverter(Label endLabel) {
		this.endLabel = endLabel;
	}

	public List<TreeStm> convert(List<BaseBlock> baseBlocks) {
		
		List<TreeStm> result = new LinkedList<>();
		for (BaseBlock baseBlock : baseBlocks) {
			result.addAll(baseBlock.body);
		}
		
		result.add(new TreeStmLABEL(endLabel));
		
		return result;
	}
}
