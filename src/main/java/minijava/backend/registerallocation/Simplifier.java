package minijava.backend.registerallocation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import minijava.util.SimpleGraph;

public class Simplifier {

	public static void simplify(SimpleGraph<ColoredNode> graph, List<SimpleGraph<ColoredNode>.Node> stack, int k) {
		
		Set<SimpleGraph<ColoredNode>.Node> nodes = new HashSet<>(graph.nodeSet());
		
		for (SimpleGraph<ColoredNode>.Node node : nodes) {
			if (node.info.color == null && node.degree() < k) {
				graph.removeNode(node);
				stack.add(node);
			}
		}
	}
}
