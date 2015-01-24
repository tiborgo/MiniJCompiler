package minijava.registerallocation;

import java.util.List;

import minijava.flowanalysis.CoalesceableTemp;
import minijava.util.SimpleGraph;

class Spiller {

	static boolean spill(SimpleGraph<CoalesceableTemp> graph, List<CoalesceableTemp> stack) {

		int maxDegree = 0;
		SimpleGraph.Node<CoalesceableTemp> maxDegreeNode = null;
		for (SimpleGraph.Node<CoalesceableTemp> n : graph.nodeSet()) {
			if (!n.info.isColored() && n.degree() > maxDegree) {
				maxDegree = n.degree();
				maxDegreeNode = n;
			}
		}

//		//if (maxDegreeNode != null) {
//		
//		/*for (SimpleGraph.Node<CoalesceableTemp> n : graph.nodeSet()) {
//			if (!n.info.isColored()/* && n.secondaryNeighbours().size() > 0 */) {
//				stack.add(n.info);
//				//graph.removeNode(n);
//				graph.deactivateNode(n);
//				return true;
//			}
//		}
//		return false;
		
		//SimpleGraph.Node<CoalesceableTemp> n = graph.nodeSet().iterator().next();
			

		//}
		
		if (maxDegreeNode != null) {
			graph.deactivateNode(maxDegreeNode);
			stack.add(maxDegreeNode.info);
			return true;
		}
		else {
			return false;
		}
	}
}
