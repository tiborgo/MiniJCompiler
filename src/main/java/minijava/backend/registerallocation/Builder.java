package minijava.backend.registerallocation;

import java.util.List;
import java.util.Map;

import minijava.backend.Assem;
import minijava.backend.livenessanalysis.ControlFlowGraphBuilder;
import minijava.backend.livenessanalysis.InterferenceGraphBuilder;
import minijava.backend.livenessanalysis.LivenessSetsBuilder;
import minijava.intermediate.FragmentProc;
import minijava.intermediate.Temp;
import minijava.util.SimpleGraph;

public class Builder {

	public static SimpleGraph<ColoredTemp> build(List<Temp> colors, FragmentProc<List<Assem>> assemFragment) {
		
		SimpleGraph<Assem> controlFlowGraph = ControlFlowGraphBuilder.build(assemFragment);
		Map<Assem, LivenessSetsBuilder.InOut> inOut = LivenessSetsBuilder.build(controlFlowGraph);
		SimpleGraph<Temp> interferenceGraph = InterferenceGraphBuilder.build(controlFlowGraph, inOut);
		
		SimpleGraph<ColoredTemp> preColoredGraph = new SimpleGraph<>(interferenceGraph.getName());

		for (SimpleGraph<Temp>.Node tNode : interferenceGraph.nodeSet()) {
			Temp color = null;
			if (colors.contains(tNode.info)) {
				color = tNode.info;
			}
			
			preColoredGraph.new Node(new ColoredTemp(tNode.info, color));
		}
		
		for (SimpleGraph<Temp>.Node tNode : interferenceGraph.nodeSet()) {
			for (SimpleGraph<Temp>.Node sNode : tNode.successors()) {
				preColoredGraph.addEdge(preColoredGraph.get(new ColoredTemp(tNode.info)), preColoredGraph.get(new ColoredTemp(sNode.info)));
			}
			for (SimpleGraph<Temp>.Node pNode : tNode.predecessors()) {
				preColoredGraph.addEdge(preColoredGraph.get(new ColoredTemp(pNode.info)), preColoredGraph.get(new ColoredTemp(tNode.info)));
			}
		}
		
		return preColoredGraph;
	}
}
