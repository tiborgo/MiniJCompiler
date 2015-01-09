package minijava.backend.registerallocation;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import minijava.intermediate.Temp;
import minijava.util.SimpleGraph;

public class Selector {

	public static List<SimpleGraph<ColoredNode>.Node> select(
			SimpleGraph<ColoredNode> graph,
			List<Temp> stack,
			List<Temp> colors,
			Map<Temp, SimpleGraph<ColoredNode>.BackupNode> backupNodes) {
		
		List<SimpleGraph<ColoredNode>.Node> spilledNodes = new LinkedList<>();
		
		for (Temp t : stack) {
			
			// Add node to simplified graph
			
			//SimpleGraph<ColoredNode>.Node n_ = simplifiedGraph.new Node(n.node);
			
			SimpleGraph<ColoredNode>.BackupNode b = backupNodes.get(t);
			graph.addBackup(b);
			SimpleGraph<ColoredNode>.Node n = graph.get(b.info);
			
			/*for  (ColoredNode st : n.successors) {
				SimpleGraph<ColoredNode>.Node s = simplifiedGraph.get(st);
				if (s != null) {
					simplifiedGraph.addEdge(n_, s);
				}
			}
			for  (ColoredNode pt : n.predecessors) {
				SimpleGraph<ColoredNode>.Node p = simplifiedGraph.get(pt);
				if (p != null) {
					simplifiedGraph.addEdge(p, n_);
				}
			}*/
			
			// Find color for node
			Set<SimpleGraph<ColoredNode>.Node> neighbours = n.neighbours();
			
			for (Temp color : colors) {
				boolean occupied = false;
				for (SimpleGraph<ColoredNode>.Node neighbour : neighbours) {
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
				spilledNodes.add(n);
			}
		}
		
		stack.clear();
		
		return spilledNodes;
	}
}
