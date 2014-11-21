package minijava.ast.visitors.baseblocks;

import java.util.ArrayList;
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

public class Tracer {

	private Tracer() {
		
	}
	
	private static Pair<BaseBlock, BaseBlock> getOptimalNextBaseBlock(BaseBlock reference, Map<Label, BaseBlock> baseBlocks) {
		
		BaseBlock optimalNextBaseBlock = null;
		
		if (reference.jump instanceof TreeStmCJUMP) {
			
			TreeStmCJUMP jump = ((TreeStmCJUMP)reference.jump);
			
			BaseBlock thenBody = baseBlocks.get(jump.ltrue);
			BaseBlock elseBody = baseBlocks.get(jump.lfalse);
			
			if (thenBody != null && elseBody == null) {
				
				List<TreeStm> referenceBody = new ArrayList<>(reference.body);
				referenceBody.set(
					referenceBody.size()-1,
					new TreeStmCJUMP(jump.rel.neg(), jump.left, jump.right, jump.lfalse, jump.ltrue)
				);
				
				reference = new BaseBlock(referenceBody);
				optimalNextBaseBlock = thenBody;
			}
			else if (thenBody == null && elseBody == null) {
				
				Label dummElseLabel = new Label();
				
				List<TreeStm> referenceBody = new ArrayList<>(reference.body);
				referenceBody.set(
					referenceBody.size()-1,
					new TreeStmCJUMP(jump.rel, jump.left, jump.right, jump.ltrue, dummElseLabel)
				);
				
				reference = new BaseBlock(referenceBody);
				
				optimalNextBaseBlock = new BaseBlock(Arrays.asList(
					new TreeStmLABEL(dummElseLabel),
					TreeStmJUMP.jumpToLabel(jump.lfalse)
				));
			} else {
				optimalNextBaseBlock = elseBody;
			}
		}
		else {
			TreeStmJUMP jump = ((TreeStmJUMP)reference.jump);
			
			if (jump.dest instanceof TreeExpNAME) {
				optimalNextBaseBlock = baseBlocks.get(((TreeExpNAME)jump.dest).label);
			} else {
				throw new UnsupportedOperationException("Only jumps to labels are implemented!");
			}
		}
		
		return new Pair<>(reference, optimalNextBaseBlock);
	}
	
	public static List<BaseBlock> trace(Map<Label, BaseBlock> baseBlocks, Label startLabel) {
		
		List<BaseBlock> result = new LinkedList<>();
		Map<Label, BaseBlock> untracedBaseBlocks = new HashMap<>(baseBlocks);
		BaseBlock reference = untracedBaseBlocks.get(startLabel);
		
		while (untracedBaseBlocks.size() > 0) {
			
			if (reference == null) {
				reference = untracedBaseBlocks.get(untracedBaseBlocks.keySet().iterator().next());
			}
			
			untracedBaseBlocks.remove(reference.label);
			Pair<BaseBlock, BaseBlock> update = getOptimalNextBaseBlock(reference, untracedBaseBlocks);
			
			result.add(update.fst);
			reference = update.snd;
			if (reference != null) { 
				untracedBaseBlocks.put(reference.label, reference);
			}
		}
		
		return result;
	}
	
	/**
	 * For testing only. Just transforms @baseBlocks to list
	 * @param baseBlocks
	 * @return
	 */
	public static List<BaseBlock> traceTrivial(Map<Label, BaseBlock> baseBlocks) {
		
		List<BaseBlock> result = new LinkedList<>();
		for (Label label : baseBlocks.keySet()) {
			result.add(baseBlocks.get(label));
		}
		return result;
	}
}
