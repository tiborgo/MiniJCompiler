package minijava.backend.registerallocation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import minijava.intermediate.Temp;
import minijava.util.SimpleGraph;

public class Builder {

	public static SimpleGraph<ColoredTemp> build (SimpleGraph<Temp> interferenceGraph, List<Temp> colors) {
		
		SimpleGraph<ColoredTemp> preColoredGraph = new SimpleGraph<>(interferenceGraph.getName());

		for (SimpleGraph<Temp>.Node tNode : interferenceGraph.nodeSet()) {
			Temp color = null;
			if (colors.contains(tNode.info)) {
				color = tNode.info;
			}
			
			preColoredGraph.new Node(new ColoredTemp(tNode.info, color));
		}
		
		for (SimpleGraph<Temp>.Node tNode : interferenceGraph.nodeSet()) {
			for (SimpleGraph<Temp>.Node sNode : tNode.successors()) {
				preColoredGraph.addEdge(preColoredGraph.get(new ColoredTemp(tNode.info)), preColoredGraph.get(new ColoredTemp(sNode.info)));
			}
			for (SimpleGraph<Temp>.Node pNode : tNode.predecessors()) {
				preColoredGraph.addEdge(preColoredGraph.get(new ColoredTemp(pNode.info)), preColoredGraph.get(new ColoredTemp(tNode.info)));
			}
		}
		
		return preColoredGraph;
	}
}
