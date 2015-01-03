package minijava.backend.registerallocation;

import java.util.List;

import minijava.util.SimpleGraph;

public class Spiller {

	public static void spill(SimpleGraph<ColoredNode> graph, List<SimpleGraph<ColoredNode>.Node> stack) {
		
		int maxDegree = 0;
		SimpleGraph<ColoredNode>.Node maxDegreeNode = null;
		for (SimpleGraph<ColoredNode>.Node n : graph.nodeSet()) {
			if (!n.info.isColored() && n.degree() > maxDegree) {
				maxDegree = n.degree();
				maxDegreeNode = n;
			}
		}

		if (maxDegreeNode != null) {
			graph.removeNode(maxDegreeNode);
			stack.add(maxDegreeNode);
		}
	}
}
