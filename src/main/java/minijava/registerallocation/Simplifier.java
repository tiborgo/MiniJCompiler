package minijava.registerallocation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import minijava.flowanalysis.CoalesceableTemp;
import minijava.util.SimpleGraph;

class Simplifier {

	static boolean simplify(SimpleGraph<CoalesceableTemp> graph, List<CoalesceableTemp> stack, int k) {

		Set<SimpleGraph.Node<CoalesceableTemp>> nodes = new HashSet<>(graph.nodeSet());
		boolean changed = false;

		for (SimpleGraph.Node<CoalesceableTemp> node : nodes) {
			if (!node.info.isMoveRelated() && node.degree() < k && !node.info.isColored()) {
				stack.add(node.info);
				graph.removeNode(node);
				changed = true;
			}
		}
		
		return changed;
	}
}
