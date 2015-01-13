package minijava.backend.registerallocation;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import minijava.backend.Assem;
import minijava.backend.i386.I386PrintAssemblyVisitor;
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
		
		int maxInterference = 0;
		I386PrintAssemblyVisitor visitor = new I386PrintAssemblyVisitor();
		Iterator<Assem> iter = assemFragment.body.iterator();
		StringBuilder inOutStringBuilder = new StringBuilder();
		
		while (iter.hasNext()) {
			
			
			Assem next = iter.next();
			String nextString = StringUtils.rightPad(next.accept(visitor), 30); 
			String padding = StringUtils.rightPad("", 30);
			String separator = StringUtils.rightPad("", 100, '-');
			
			LivenessSetsBuilder.InOut inOutN = inOut.get(next);
			if (inOutN.in.size() > maxInterference) {
				maxInterference = inOutN.in.size();
			}
			if (inOutN.out.size() > maxInterference) {
				maxInterference = inOutN.out.size();
			}
			
			/*inOutStringBuilder
				.append(nextString)
				.append("   in: ")
				.append(inOutN.in)
				.append(System.lineSeparator())
				.append(padding)
				.append("   out:")
				.append(inOutN.out)
				.append(System.lineSeparator())
				.append(separator)
				.append(System.lineSeparator());*/
		}
		inOutStringBuilder
			.append("Max interference: ")
			.append(maxInterference);
		System.out.println(inOutStringBuilder);
		
		
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
