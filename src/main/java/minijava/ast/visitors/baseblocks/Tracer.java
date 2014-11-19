package minijava.ast.visitors.baseblocks;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import minijava.intermediate.Label;

public class Tracer {

	public List<BaseBlock> trace(Map<Label, BaseBlock> baseBlocks) {
		
		List<BaseBlock> result = new LinkedList<>();
		for (Label label : baseBlocks.keySet()) {
			result.add(baseBlocks.get(label));
		}
		
		return result;
	}
}
