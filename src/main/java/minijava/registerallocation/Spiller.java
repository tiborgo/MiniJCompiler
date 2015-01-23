package minijava.registerallocation;

import java.util.List;

import minijava.flowanalysis.CoalesceableTemp;
import minijava.util.SimpleGraph;

class Spiller {

	static void spill(SimpleGraph<CoalesceableTemp> graph, List<CoalesceableTemp> stack) {

		/*int maxDegree = 0;
		SimpleGraph.Node<CoalesceableTemp> maxDegreeNode = null;
		for (SimpleGraph.Node<CoalesceableTemp> n : graph.nodeSet()) {
			if (!n.info.isColored() && n.degree() > maxDegree) {
				maxDegree = n.degree();
				maxDegreeNode = n;
			}
		}*/

		//if (maxDegreeNode != null) {
		
		for (SimpleGraph.Node<CoalesceableTemp> n : graph.nodeSet()) {
			if (!n.info.isColored() && !n.info.isMoveRelated()) {
				stack.add(n.info);
				graph.removeNode(n);
				break;
			}
		}
		
		//SimpleGraph.Node<CoalesceableTemp> n = graph.nodeSet().iterator().next();
			

		//}
	}
}
