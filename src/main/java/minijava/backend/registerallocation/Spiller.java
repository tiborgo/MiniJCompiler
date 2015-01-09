package minijava.backend.registerallocation;

import java.util.List;

import minijava.util.SimpleGraph;
import minijava.intermediate.Temp;

public class Spiller {

	public static void spill(SimpleGraph<ColoredNode> graph, List<Temp> stack) {
		
		int maxDegree = 0;
		SimpleGraph<ColoredNode>.Node maxDegreeNode = null;
		for (SimpleGraph<ColoredNode>.Node n : graph.nodeSet()) {
			if (!n.info.isColored() && n.degree() > maxDegree) {
				maxDegree = n.degree();
				maxDegreeNode = n;
			}
		}

		if (maxDegreeNode != null) {
			stack.add(maxDegreeNode.info.temp);
			graph.removeNode(maxDegreeNode);
			
		}
	}
}
