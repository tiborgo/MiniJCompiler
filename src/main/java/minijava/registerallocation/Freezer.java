package minijava.registerallocation;

import java.util.List;
import java.util.Set;

import minijava.flowanalysis.CoalesceableTemp;
import minijava.translate.layout.Temp;
import minijava.util.SimpleGraph;
import minijava.util.SimpleGraph.Node;

public class Freezer {

	public static boolean freeze(SimpleGraph<CoalesceableTemp> graph, List<CoalesceableTemp> stack, int k) {
		
		//boolean freezed = false;
		
		for (SimpleGraph.Node<CoalesceableTemp> a : graph.nodeSet()) {
			if (a.degree() < k && a.secondaryNeighbours().size() > 0) {
				//stack.add(n.info);
				//((graph.removeNode(n);
				
				
				Set<SimpleGraph.Node<CoalesceableTemp>> Bs = a.secondaryNeighbours();
				for (Node<CoalesceableTemp> b : Bs) {
					//Node<CoalesceableTemp> b = graph.get(new CoalesceableTemp(tB));
				
				
					//if (b != null) {
						//b.info.removePartner(a.info.temp);
					//}
					
					graph.removeSecondaryEdge(a, b);
					graph.removeSecondaryEdge(b, a);
					return true;
				}
				//a.info.free();
				//freezed = true;
			}
		}
		return false;
	}
}
