package minijava.backend.registerallocation;

import java.util.List;

import minijava.util.SimpleGraph;

public class Spiller {

	public static void spill(SimpleGraph<ColoredTemp> graph, List<ColoredTemp> stack) {
		
		int maxDegree = 0;
		SimpleGraph<ColoredTemp>.Node maxDegreeNode = null;
		for (SimpleGraph<ColoredTemp>.Node n : graph.nodeSet()) {
			if (!n.info.isColored() && n.degree() > maxDegree) {
				maxDegree = n.degree();
				maxDegreeNode = n;
			}
		}

		if (maxDegreeNode != null) {
			stack.add(maxDegreeNode.info);
			graph.removeNode(maxDegreeNode);
			
		}
	}
}
