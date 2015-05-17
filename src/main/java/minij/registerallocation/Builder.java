package minij.registerallocation;

import java.util.List;
import java.util.Set;

import minij.flowanalysis.CoalesceableTemp;
import minij.flowanalysis.FlowAnalyserException;
import minij.translate.layout.Temp;
import minij.util.Pair;
import minij.util.SimpleGraph;

class Builder {

	static void build(SimpleGraph<CoalesceableTemp> interferenceGraph, List<Temp> colors) throws FlowAnalyserException {

		// Add secondary edges between temps that are connected through a move
		for (SimpleGraph.Node<CoalesceableTemp> a : interferenceGraph.nodeSet()) {
			for (Temp tB : a.info.getPartners()) {
				SimpleGraph.Node<CoalesceableTemp> b = interferenceGraph.get(new CoalesceableTemp(tB));
				
				Set<Pair<Temp, Temp>> moveTempsSet = a.info.getMoveTemps(b.info.temp);
				
				for (Pair<Temp, Temp> moveTemps: moveTempsSet) {
					
					if (moveTemps.fst.equals(a.info.temp)) {
						// dont include duplicate edges or edges between interfering nodes, because these nodes cannot be coalesced anyway
						if (!interferenceGraph.hasEdge(a, b) && !interferenceGraph.hasSecondaryEdge(b, a)) {
							interferenceGraph.addSecondaryEdge(b, a);
						}
					}
					else {
						// dont include duplicate edges or edges between interfering nodes, because these nodes cannot be coalesced anyway
						if (!interferenceGraph.hasEdge(a, b) && !interferenceGraph.hasSecondaryEdge(a, b)) {
							interferenceGraph.addSecondaryEdge(a, b);
						}
					}
				}
				
				
			}
		}
		
		for (SimpleGraph.Node<CoalesceableTemp> tNode : interferenceGraph.nodeSet()) {
			if (colors.contains(tNode.info.temp)) {
				tNode.info.color = tNode.info.temp;
			}
		}
	}
}
