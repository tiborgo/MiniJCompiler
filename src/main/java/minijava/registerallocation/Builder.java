package minijava.registerallocation;

import java.util.List;

import minijava.flowanalysis.FlowAnalyserException;
import minijava.instructionselection.assems.Assem;
import minijava.translate.layout.FragmentProc;
import minijava.translate.layout.Temp;
import minijava.util.SimpleGraph;

class Builder {

	static SimpleGraph<ColoredTemp> build(SimpleGraph<Temp> interferenceGraph, List<Temp> colors, FragmentProc<List<Assem>> assemFragment) throws FlowAnalyserException {

		SimpleGraph<ColoredTemp> preColoredGraph = new SimpleGraph<>(interferenceGraph.getName());

		for (SimpleGraph.Node<Temp> tNode : interferenceGraph.nodeSet()) {
			Temp color = null;
			if (colors.contains(tNode.info)) {
				color = tNode.info;
			}

			preColoredGraph.addNode(new ColoredTemp(tNode.info, color));
		}

		for (SimpleGraph.Node<Temp> tNode : interferenceGraph.nodeSet()) {
			for (SimpleGraph.Node<Temp> sNode : tNode.successors()) {
				preColoredGraph.addEdge(preColoredGraph.get(new ColoredTemp(tNode.info)), preColoredGraph.get(new ColoredTemp(sNode.info)));
			}
			for (SimpleGraph.Node<Temp> pNode : tNode.predecessors()) {
				preColoredGraph.addEdge(preColoredGraph.get(new ColoredTemp(pNode.info)), preColoredGraph.get(new ColoredTemp(tNode.info)));
			}
		}

		return preColoredGraph;
	}
}
