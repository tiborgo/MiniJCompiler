package minijava.flowanalysis;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import minijava.instructionselection.assems.Assem;
import minijava.translate.layout.Label;
import minijava.util.SimpleGraph;

class ControlFlowGraphBuilder {
	static SimpleGraph<Assem> build(List<Assem> body, String methodName) {

		SimpleGraph<Assem> graph = new SimpleGraph<>(methodName, true);
		List<SimpleGraph.Node<Assem>> jumps = new LinkedList<>();
		Map<Label, SimpleGraph.Node<Assem>> labelNodes = new HashMap<>();

		SimpleGraph.Node<Assem> previousNode = null;
		Iterator<Assem> iter = body.iterator();

		while (iter.hasNext()) {

			SimpleGraph.Node<Assem> currentNode = graph.addNode(iter.next());

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

		for (SimpleGraph.Node<Assem> srcNode : jumps) {
			List<Label> dstLabels = srcNode.info.jumps();
			for (Label dstLabel : dstLabels) {
				SimpleGraph.Node<Assem> dstNode = labelNodes.get(dstLabel);
				graph.addEdge(srcNode, dstNode);
			}
		}

		return graph;
	}
}
