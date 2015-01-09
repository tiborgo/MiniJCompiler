package minijava.backend.registerallocation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import minijava.intermediate.Temp;
import minijava.util.SimpleGraph;

public class Builder {

	public static SimpleGraph<ColoredTemp> build (SimpleGraph<Temp> interferenceGraph, List<Temp> colors) {
		
		SimpleGraph<ColoredTemp> coloredInterferenceGraph = new SimpleGraph<>(interferenceGraph.getName());
		Map<Temp, SimpleGraph<ColoredTemp>.Node> nodes = new HashMap<>();

		for (SimpleGraph<Temp>.Node tNode : interferenceGraph.nodeSet()) {
			Temp color = null;
			if (colors.contains(tNode.info)) {
				color = tNode.info;
			}
			
			SimpleGraph<ColoredTemp>.Node node = coloredInterferenceGraph.new Node(new ColoredTemp(tNode.info, color));
			nodes.put(tNode.info, node);
		}
		
		for (SimpleGraph<Temp>.Node tNode : interferenceGraph.nodeSet()) {
			for (SimpleGraph<Temp>.Node sNode : tNode.successors()) {
				coloredInterferenceGraph.addEdge(nodes.get(tNode.info), nodes.get(sNode.info));
			}
			for (SimpleGraph<Temp>.Node pNode : tNode.predecessors()) {
				coloredInterferenceGraph.addEdge(nodes.get(pNode.info), nodes.get(tNode.info));
			}
		}
		
		return coloredInterferenceGraph;
	}
}
