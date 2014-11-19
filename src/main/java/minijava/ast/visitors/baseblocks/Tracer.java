package minijava.ast.visitors.baseblocks;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import minijava.intermediate.Label;

public class Tracer {

	private Tracer() {
		
	}
	
	public static List<BaseBlock> trace(Map<Label, BaseBlock> baseBlocks) {
		
		LinkedList<BaseBlock> result = new LinkedList<>();
		LinkedList<Label> untracedBlockKeys = new LinkedList<>(baseBlocks.keySet());
		
		/*while (untracedBlockKeys.size() > 0) {
			result.
		}
		
		List<BaseBlock> result = new LinkedList<>();*/
		for (Label label : baseBlocks.keySet()) {
			result.add(baseBlocks.get(label));
		}
		
		return result;
	}
}
