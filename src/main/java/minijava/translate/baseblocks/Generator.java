package minijava.translate.baseblocks;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import minijava.translate.layout.Label;
import minijava.translate.tree.TreeStm;
import minijava.translate.tree.TreeStmCJUMP;
import minijava.translate.tree.TreeStmJUMP;
import minijava.translate.tree.TreeStmLABEL;

public class Generator {

	/**
	 * Class that groups a number of code blocks while providing a common entry and a common exit point.
	 */
	public static class BaseBlockContainer {
		
		public final Map<Label, BaseBlock> baseBlocks;
		public final Label startLabel;
		public final Label endLabel;
		
		private BaseBlockContainer(Map<Label, BaseBlock> baseBlocks, Label startLabel, Label endLabel) {
			this.baseBlocks = baseBlocks;
			this.startLabel = startLabel;
			this.endLabel = endLabel;
		}
	}

	// TODO: Make Generator a singleton instead of using static methods
	/**
	 * Creates {@code BaseBlock}s from the specified list of statements and returns them as a {@code BaseBlockContainer}
	 * with the specified exit point.
	 * @param stms Canonicalized list of statements.
	 * @param endLabel Common exit point.
	 * @return Object containing base blocks that were constructed from the statement list.
	 */
	public static BaseBlockContainer generate(List<TreeStm> stms) {
		
		Map<Label, BaseBlock> baseBlocks = new HashMap<>();
		
		List<TreeStm> currentBaseBlock = new LinkedList<>();
		Label currentBaseBlockLabel;
		int i = 1;
		
		Label startLabel;
		if (!(stms.get(0) instanceof TreeStmLABEL)) {
			startLabel = new Label();
			currentBaseBlockLabel = startLabel;
			currentBaseBlock.add(new TreeStmLABEL(currentBaseBlockLabel));
		}
		else {
			startLabel = ((TreeStmLABEL)stms.get(0)).label;
			currentBaseBlockLabel = startLabel;
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
			
			if (currentBaseBlock.size() == 1) {
				currentBaseBlock.add(TreeStmJUMP.jumpToLabel(((TreeStmLABEL)stms.get(i-1)).label));
			}
			
			baseBlocks.put(
				currentBaseBlockLabel,
				new BaseBlock(currentBaseBlock)
			);
			currentBaseBlock = new LinkedList<>();
			currentBaseBlockLabel = ((TreeStmLABEL)stms.get(i-1)).label;
			currentBaseBlock.add(stms.get(i-1));
		}
		
		currentBaseBlock.add(stms.get(stms.size()-1));

		Label endLabel = new Label();
		if (!((stms.get(stms.size()-1) instanceof TreeStmJUMP) ||
				(stms.get(stms.size()-1) instanceof TreeStmCJUMP))) {

			currentBaseBlock.add(TreeStmJUMP.jumpToLabel(endLabel));
		}
		
		baseBlocks.put(
			currentBaseBlockLabel,
			new BaseBlock(currentBaseBlock)
		);
		
		return new BaseBlockContainer(baseBlocks, startLabel, endLabel);
	}
}
