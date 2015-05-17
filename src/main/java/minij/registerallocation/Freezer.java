package minij.registerallocation;

import java.util.List;
import java.util.Set;

import minij.flowanalysis.CoalesceableTemp;
import minij.util.SimpleGraph;
import minij.util.SimpleGraph.Node;

public class Freezer {

	public static boolean freeze(SimpleGraph<CoalesceableTemp> graph, List<CoalesceableTemp> stack, int k) {

		for (SimpleGraph.Node<CoalesceableTemp> a : graph.nodeSet()) {
			if (a.degree() < k && a.secondaryNeighbours().size() > 0) {

				Set<SimpleGraph.Node<CoalesceableTemp>> Bs = a.secondaryNeighbours();
				for (Node<CoalesceableTemp> b : Bs) {					
					graph.removeSecondaryEdge(a, b);
					graph.removeSecondaryEdge(b, a);
					return true;
				}
			}
		}
		return false;
	}
}
