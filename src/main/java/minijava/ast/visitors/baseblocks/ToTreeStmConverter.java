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
			return new Pair<>(((TreeStmCJUMP)jump).ltrue, ((TreeStmCJUMP)jump).lfalse);
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
		Map<Label, Integer> jumpsCount = new HashMap<>();
		for (BaseBlock baseBlock : remainingBaseBlocks) {
			Pair<Label, Label> labels = getJumpedToLabels(baseBlock.jump);
			if (!jumpsCount.containsKey(labels.fst)) {
				jumpsCount.put(labels.fst, 1);
			} else {
				jumpsCount.put(labels.fst, jumpsCount.get(labels.fst)+1);
			}
			if (labels.snd != null) {
				if (!jumpsCount.containsKey(labels.snd)) {
					jumpsCount.put(labels.snd, 1);
				} else {
					jumpsCount.put(labels.snd, jumpsCount.get(labels.snd)+1);
				}
			}
		}
		
		while (remainingBaseBlocks.size() > 0) {
				
			Pair<Label, Label> labels = getJumpedToLabels(remainingBaseBlocks.get(0).jump);
			
			if (remainingBaseBlocks.size() > 1 &&
					labels.snd == null &&
					jumpsCount.get(labels.fst) == 1 &&
					labels.fst.equals(remainingBaseBlocks.get(1).label)) {
				
				result.addAll(remainingBaseBlocks.get(0).body.subList(0, remainingBaseBlocks.get(0).body.size()-1));
				result.addAll(remainingBaseBlocks.get(1).body.subList(1, remainingBaseBlocks.get(1).body.size()));
				remainingBaseBlocks.remove(0);
				remainingBaseBlocks.remove(0);
			}
			else {
				result.addAll(remainingBaseBlocks.get(0).body);
				remainingBaseBlocks.remove(0);
			}
		}
			
		result.add(new TreeStmLABEL(endLabel));
		
		return result;
	}
}
