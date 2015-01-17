package minijava.backend.registerallocation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import minijava.util.SimpleGraph;

public class Simplifier {

	public static void simplify(SimpleGraph<ColoredTemp> graph, List<ColoredTemp> stack, int k) {

		Set<SimpleGraph.Node<ColoredTemp>> nodes = new HashSet<>(graph.nodeSet());

		for (SimpleGraph.Node<ColoredTemp> node : nodes) {
			if (node.info.color == null && node.degree() < k) {
				stack.add(node.info);
				graph.removeNode(node);
			}
		}
	}
}
