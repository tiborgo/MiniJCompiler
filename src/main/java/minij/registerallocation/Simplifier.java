package minij.registerallocation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import minij.flowanalysis.CoalesceableTemp;
import minij.util.SimpleGraph;

class Simplifier {

	static boolean simplify(SimpleGraph<CoalesceableTemp> graph, List<CoalesceableTemp> stack, int k) {

		boolean changed = false;
		boolean loopChanged ;
		
		do {
			
			loopChanged = false;
			
			Set<SimpleGraph.Node<CoalesceableTemp>> nodes = new HashSet<>(graph.nodeSet());
	
			for (SimpleGraph.Node<CoalesceableTemp> node : nodes) {
	
				if (node.secondaryNeighbours().size() == 0 && node.degree() < k && !node.info.isColored()) {
					stack.add(node.info);
					graph.deactivateNode(node);
					changed = true;
					loopChanged = true;
				}
			}
		}
		while(loopChanged);
		
		return changed;
	}
}
