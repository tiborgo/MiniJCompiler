package minij.registerallocation;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import minij.flowanalysis.CoalesceableTemp;
import minij.translate.layout.Temp;
import minij.util.SimpleGraph;

class Selector {

	static List<Temp> select(
			SimpleGraph<CoalesceableTemp> graph,
			List<CoalesceableTemp> stack,
			List<Temp> colors) {

		List<Temp> spilledNodes = new LinkedList<>();
		Collections.reverse(stack);

		for (CoalesceableTemp t : stack) {

			SimpleGraph.Node<CoalesceableTemp> n = graph.get(t);
			graph.activateNode(n);

			// Find color for node
			Set<SimpleGraph.Node<CoalesceableTemp>> neighbours = n.neighbours();

			for (Temp color : colors) {
				boolean occupied = false;
				for (SimpleGraph.Node<CoalesceableTemp> neighbour : neighbours) {
					if (color.equals(neighbour.info.color)) {
						occupied = true;
						break;
					}
				}
				if (!occupied) {
					n.info.color = color;
					break;
				}
			}

			if (n.info.color == null) {
				spilledNodes.add(n.info.temp);
			}
		}

		stack.clear();

		return spilledNodes;
	}
}
