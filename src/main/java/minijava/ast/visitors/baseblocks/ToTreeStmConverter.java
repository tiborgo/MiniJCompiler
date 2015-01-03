package minijava.ast.visitors.baseblocks;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import minijava.intermediate.Label;
import minijava.intermediate.tree.TreeExpNAME;
import minijava.intermediate.tree.TreeStm;
import minijava.intermediate.tree.TreeStmCJUMP;
import minijava.intermediate.tree.TreeStmJUMP;
import minijava.intermediate.tree.TreeStmLABEL;
import minijava.util.Pair;

public class ToTreeStmConverter {
	
	private ToTreeStmConverter() {
	}
	
	private static Pair<Label, Label> getJumpedToLabels(TreeStm jump) {
		if (jump instanceof TreeStmJUMP) {
			if (((TreeStmJUMP)jump).dest instanceof TreeExpNAME) {
				
				return new Pair<>(((TreeExpNAME)((TreeStmJUMP)jump).dest).label, null);
				
			} else {
				throw new UnsupportedOperationException("Only jumps to labels are implemented!");
			}
		}
		else if (jump instanceof TreeStmCJUMP) {
			return new Pair<>(((TreeStmCJUMP)jump).lfalse, ((TreeStmCJUMP)jump).ltrue);
		}
		else {
			throw new IllegalArgumentException("'jump' must either be instance of TreeStmJUMP or TreeStmCJUMP");
		}
	}

	public static List<TreeStm> convert(List<BaseBlock> baseBlocks, Label startLabel, Label endLabel) {
		
		List<TreeStm> result = new LinkedList<>();
		List<BaseBlock> remainingBaseBlocks = new LinkedList<>(baseBlocks);
		
		remainingBaseBlocks.add(0, new BaseBlock(
			Arrays.asList(
				new TreeStmLABEL(new Label()),
				TreeStmJUMP.jumpToLabel(startLabel)
			)
		));
		
		// Check how many jumps to each label are in the base blocks
		Map<Label, Integer> jumpsCounts = new HashMap<>();
		for (BaseBlock baseBlock : remainingBaseBlocks) {
			Pair<Label, Label> labels = getJumpedToLabels(baseBlock.jump);
			if (!jumpsCounts.containsKey(labels.fst)) {
				jumpsCounts.put(labels.fst, 1);
			} else {
				jumpsCounts.put(labels.fst, jumpsCounts.get(labels.fst)+1);
			}
			if (labels.snd != null) {
				if (!jumpsCounts.containsKey(labels.snd)) {
					jumpsCounts.put(labels.snd, 1);
				} else {
					jumpsCounts.put(labels.snd, jumpsCounts.get(labels.snd)+1);
				}
			}
		}
		
		Label previousJumpDestLabel = null;
		
		while (remainingBaseBlocks.size() > 0) {
				
			Pair<Label, Label> labels = getJumpedToLabels(remainingBaseBlocks.get(0).jump);
			BaseBlock baseBlock = remainingBaseBlocks.get(0);
			
			int startIndex = 0;
			int endIndex = baseBlock.body.size();
			
			if (labels.snd == null) {
				
				if ((remainingBaseBlocks.size() > 1 && labels.fst.equals(remainingBaseBlocks.get(1).label)) ||
						(remainingBaseBlocks.size() == 0 && labels.fst.equals(endLabel))) {
					
					endIndex--;
				}
			}
			
			Integer jumpsCount = jumpsCounts.get(baseBlock.label);
			if (jumpsCount == null || jumpsCount <= 1) {
				if (previousJumpDestLabel == null || previousJumpDestLabel.equals(baseBlock.label)) {
					startIndex++;
				}
			}
				
			result.addAll(baseBlock.body.subList(startIndex, endIndex));
			
			remainingBaseBlocks.remove(0);
			
			previousJumpDestLabel = labels.fst;
		}
			
		if (jumpsCounts.get(endLabel) > 1 || !previousJumpDestLabel.equals(endLabel)) {
			result.add(new TreeStmLABEL(endLabel));
		}
		
		return result;
	}
}
