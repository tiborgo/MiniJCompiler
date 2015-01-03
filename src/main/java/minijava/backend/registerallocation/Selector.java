package minijava.backend.registerallocation;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import minijava.intermediate.Temp;
import minijava.util.SimpleGraph;

public class Selector {

	public static List<SimpleGraph<ColoredNode>.Node> select(
			SimpleGraph<Temp> graph, SimpleGraph<ColoredNode> simplifiedGraph,
			List<SimpleGraph<ColoredNode>.Node> stack, List<Temp> colors) {
		
		List<SimpleGraph<ColoredNode>.Node> spilledNodes = new LinkedList<>();
		
		for (SimpleGraph<ColoredNode>.Node n : stack) {
			
			// Add node to simplified graph
			SimpleGraph<ColoredNode>.Node n_ = simplifiedGraph.new Node(n.info);
			for  (SimpleGraph<ColoredNode>.Node s : n.successors()) {
				if (simplifiedGraph.nodeSet().contains(s)) {
					simplifiedGraph.addEdge(n_, s);
				}
			}
			for  (SimpleGraph<ColoredNode>.Node p : n.predecessors()) {
				simplifiedGraph.addEdge(p, n_);
			}
			
			// Find color for node
			Set<SimpleGraph<ColoredNode>.Node> neighbours = n.neighbours();
			
			for (Temp color : colors) {
				for (SimpleGraph<ColoredNode>.Node neighbour : neighbours) {
					if (!color.equals(neighbour.info.color)) {
						n_.info.color = color;
					}
				}
			}
			
			if (n_.info.color == null) {
				spilledNodes.add(n_);
			}
		}
		
		stack.clear();
		
		return spilledNodes;
	}
}
