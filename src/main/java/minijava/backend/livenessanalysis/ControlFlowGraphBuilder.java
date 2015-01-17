package minijava.backend.livenessanalysis;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import minijava.backend.Assem;
import minijava.intermediate.FragmentProc;
import minijava.intermediate.Label;
import minijava.util.SimpleGraph;

public class ControlFlowGraphBuilder {
	public static SimpleGraph<Assem> build(FragmentProc<List<Assem>> frag) {

		SimpleGraph<Assem> graph = new SimpleGraph<>(frag.frame.getName().toString());
		List<SimpleGraph<Assem>.Node> jumps = new LinkedList<>();
		Map<Label, SimpleGraph<Assem>.Node> labelNodes = new HashMap<>();

		SimpleGraph<Assem>.Node previousNode = null;
		Iterator<Assem> iter = frag.body.iterator();

		while (iter.hasNext()) {

			SimpleGraph<Assem>.Node currentNode = graph.addNode(iter.next());

			if (previousNode != null && previousNode.info.isFallThrough()) {
				graph.addEdge(previousNode, currentNode);
			}

			if (currentNode.info.jumps().size() > 0){
				jumps.add(currentNode);
			}

			if (currentNode.info.isLabel() != null) {
				labelNodes.put(currentNode.info.isLabel(), currentNode);
			}

			previousNode = currentNode;
		}

		for (SimpleGraph<Assem>.Node srcNode : jumps) {
			List<Label> dstLabels = srcNode.info.jumps();
			for (Label dstLabel : dstLabels) {
				SimpleGraph<Assem>.Node dstNode = labelNodes.get(dstLabel);
				graph.addEdge(srcNode, dstNode);
			}
		}

		return graph;
	}
}
