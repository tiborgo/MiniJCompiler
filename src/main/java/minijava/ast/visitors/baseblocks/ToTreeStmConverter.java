package minijava.ast.visitors.baseblocks;

import java.util.LinkedList;
import java.util.List;

import minijava.intermediate.Label;
import minijava.intermediate.tree.TreeStm;
import minijava.intermediate.tree.TreeStmJUMP;
import minijava.intermediate.tree.TreeStmLABEL;

public class ToTreeStmConverter {
	
	private ToTreeStmConverter() {
	}

	public static List<TreeStm> convert(List<BaseBlock> baseBlocks, Label startLabel, Label endLabel) {
		
		List<TreeStm> result = new LinkedList<>();
		
		if (!baseBlocks.get(0).label.equals(startLabel)) {
			result.add(TreeStmJUMP.jumpToLabel(startLabel));
		}
		
		for (BaseBlock baseBlock : baseBlocks) {
			result.addAll(baseBlock.body);
		}
		
		result.add(new TreeStmLABEL(endLabel));
		
		return result;
	}
}
