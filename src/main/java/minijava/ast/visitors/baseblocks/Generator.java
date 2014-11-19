package minijava.ast.visitors.baseblocks;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import minijava.intermediate.Label;
import minijava.intermediate.tree.TreeStm;
import minijava.intermediate.tree.TreeStmCJUMP;
import minijava.intermediate.tree.TreeStmJUMP;
import minijava.intermediate.tree.TreeStmLABEL;

public class Generator {
	
	public final Label endLabel;

	public Generator() {
		this.endLabel = new Label();
	}
	
	public Map<Label, BaseBlock> generate(List<TreeStm> stms) {
		
		Map<Label, BaseBlock> baseBlocks = new HashMap<>();
		
		List<TreeStm> currentBaseBlock = new LinkedList<>();
		Label currentBaseBlockLabel;
		int i = 1;
		
		if (!(stms.get(0) instanceof TreeStmLABEL)) {
			currentBaseBlockLabel = new Label();
			currentBaseBlock.add(new TreeStmLABEL(currentBaseBlockLabel));
		}
		else {
			currentBaseBlockLabel = ((TreeStmLABEL)stms.get(0)).label;
			currentBaseBlock.add(stms.get(0));
			i++;
		}
		
		for (; i < stms.size(); i++) {
			
			while (i < stms.size() && !(stms.get(i-1) instanceof TreeStmLABEL)) {
				
				currentBaseBlock.add(stms.get(i-1));
				
				// new base block
				if (stms.get(i) instanceof TreeStmLABEL) {
					
					if (!(stms.get(i-1) instanceof TreeStmJUMP || 
							stms.get(i-1) instanceof TreeStmCJUMP)) {
						
						currentBaseBlock.add(TreeStmJUMP.jumpToLabel(((TreeStmLABEL)stms.get(i)).label));
					}
				}
				else if (!(stms.get(i) instanceof TreeStmLABEL) &&
						(stms.get(i-1) instanceof TreeStmJUMP ||
								stms.get(i-1) instanceof TreeStmCJUMP)) {
					// Dead code -> skip
					do {
						i++;
					}
					while(i < stms.size() && !(stms.get(i) instanceof TreeStmLABEL));
				}
				
				i++;
			}
			
			if (i == stms.size()) {
				break;
			}
			
			baseBlocks.put(
				currentBaseBlockLabel,
				new BaseBlock(
					currentBaseBlockLabel,
					currentBaseBlock.get(currentBaseBlock.size()-1),
					currentBaseBlock
				)
			);
			currentBaseBlock = new LinkedList<>();
			currentBaseBlockLabel = ((TreeStmLABEL)stms.get(i-1)).label;
			currentBaseBlock.add(stms.get(i-1));
		}
		
		currentBaseBlock.add(stms.get(stms.size()-1));
		
		if (!((stms.get(stms.size()-1) instanceof TreeStmJUMP) ||
				(stms.get(stms.size()-1) instanceof TreeStmCJUMP))) {

			currentBaseBlock.add(TreeStmJUMP.jumpToLabel(endLabel));
		}
		
		baseBlocks.put(
			currentBaseBlockLabel,
			new BaseBlock(
				currentBaseBlockLabel,
				currentBaseBlock.get(currentBaseBlock.size()-1),
				currentBaseBlock
			)
		);
		
		return baseBlocks;
	}
}
