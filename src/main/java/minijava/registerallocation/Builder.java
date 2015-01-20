package minijava.registerallocation;

import java.util.List;

import minijava.flowanalysis.CoalesceableTemp;
import minijava.flowanalysis.FlowAnalyserException;
import minijava.translate.layout.Temp;
import minijava.util.SimpleGraph;

class Builder {

	static void build(SimpleGraph<CoalesceableTemp> interferenceGraph, List<Temp> colors) throws FlowAnalyserException {

		for (SimpleGraph.Node<CoalesceableTemp> tNode : interferenceGraph.nodeSet()) {
			if (colors.contains(tNode.info.temp)) {
				tNode.info.color = tNode.info.temp;
			}
		}
	}
}
