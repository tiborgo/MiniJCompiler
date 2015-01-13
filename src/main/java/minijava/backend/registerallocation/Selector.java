package minijava.backend.registerallocation;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import minijava.intermediate.Temp;
import minijava.util.SimpleGraph;

public class Selector {

	public static List<Temp> select(
			SimpleGraph<ColoredTemp> graph,
			List<ColoredTemp> stack,
			List<Temp> colors,
			Map<ColoredTemp, SimpleGraph<ColoredTemp>.BackupNode> graphBackup) {
		
		List<Temp> spilledNodes = new LinkedList<>();
		Collections.reverse(stack);
		
		for (ColoredTemp t : stack) {
			
			// Add node to simplified graph
			
			
			SimpleGraph<ColoredTemp>.BackupNode b = graphBackup.get(t);
			graph.restore(b);
			SimpleGraph<ColoredTemp>.Node n = graph.get(t);
			
			// Find color for node
			Set<SimpleGraph<ColoredTemp>.Node> neighbours = n.neighbours();
			
			for (Temp color : colors) {
				boolean occupied = false;
				for (SimpleGraph<ColoredTemp>.Node neighbour : neighbours) {
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
