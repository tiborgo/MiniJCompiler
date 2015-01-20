package minijava.registerallocation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import minijava.flowanalysis.CoalesceableTemp;
import minijava.util.SimpleGraph;

class Simplifier {

	static void simplify(SimpleGraph<CoalesceableTemp> graph, List<CoalesceableTemp> stack, int k) {

		Set<SimpleGraph.Node<CoalesceableTemp>> nodes = new HashSet<>(graph.nodeSet());

		for (SimpleGraph.Node<CoalesceableTemp> node : nodes) {
			if (node.info.color == null && node.degree() < k) {
				stack.add(node.info);
				graph.removeNode(node);
			}
		}
	}
}
