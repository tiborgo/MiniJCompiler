package minijava.registerallocation;

import java.util.List;
import java.util.Set;

import minijava.flowanalysis.CoalesceableTemp;
import minijava.translate.layout.Temp;
import minijava.util.SimpleGraph;
import minijava.util.SimpleGraph.Node;

public class Freezer {

	public static boolean freeze(SimpleGraph<CoalesceableTemp> graph, List<CoalesceableTemp> stack, int k) {
		
		boolean freezed = false;
		
		for (SimpleGraph.Node<CoalesceableTemp> a : graph.nodeSet()) {
			if (a.degree() < k && a.info.isMoveRelated()) {
				//stack.add(n.info);
				//((graph.removeNode(n);
				
				
				Set<Temp> tBs = a.info.getPartners();
				for (Temp tB : tBs) {
					Node<CoalesceableTemp> b = graph.get(new CoalesceableTemp(tB));
				
				
					if (b != null) {
						b.info.removePartner(a.info.temp);
					}
				}
				a.info.free();
				freezed = true;
			}
		}
		return freezed;
	}
}
