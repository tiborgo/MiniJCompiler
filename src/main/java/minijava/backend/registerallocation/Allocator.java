package minijava.backend.registerallocation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import minijava.backend.Assem;
import minijava.backend.MachineSpecifics;
import minijava.intermediate.FragmentProc;
import minijava.intermediate.Temp;
import minijava.util.Function;
import minijava.util.SimpleGraph;

public class Allocator {
	
	/*static class StackNode {
		public final ColoredNode node;
		public final Set<ColoredNode> successors = new HashSet<>();
		public final Set<ColoredNode> predecessors = new HashSet<>();
		
		public StackNode(SimpleGraph<ColoredNode>.Node node) {
			
			this.node = node.info;
			
			for (SimpleGraph<ColoredNode>.Node s : node.successors()) {
				successors.add(s.info);
			}
			for (SimpleGraph<ColoredNode>.Node p : node.predecessors()) {
				predecessors.add(p.info);
			}
		}
	}*/
	

	public static SimpleGraph<ColoredNode> allocate (SimpleGraph<Temp> interferenceGraph, FragmentProc<List<Assem>> assemFragment, MachineSpecifics machineSpecifics) {
		
		// slide 267
		
		List<Temp> colors = Arrays.asList(machineSpecifics.getGeneralPurposeRegisters());
		int k = colors.size();
		
		
		// BUILD
		SimpleGraph<ColoredNode> graph = Builder.build(interferenceGraph, colors);
		//SimpleGraph<ColoredNode> simplifiedGraph = new SimpleGraph<>(graph);
		
		Map<Temp, SimpleGraph<ColoredNode>.BackupNode> backupNodes = new HashMap<>();
		for (SimpleGraph<ColoredNode>.Node n : graph.nodeSet()) {
			backupNodes.put(n.info.temp, n.backup());
		}
		
		int coloredNodesCount = 0;
		for (SimpleGraph<ColoredNode>.Node n : graph.nodeSet()) {
			if (n.info.isColored()) coloredNodesCount++;
		}
		
		List<Temp> stack = new LinkedList<>();
		//SimpleGraph<ColoredNode> simplifiedGraph = new SimpleGraph<>(graph);
		do {
			// SIMPLIFY
			Simplifier.simplify(graph, stack, k);
			
			// SPILL
			Spiller.spill(graph, stack);
		}
		while(graph.nodeSet().size() > coloredNodesCount);
		
		// SELECT
		List<SimpleGraph<ColoredNode>.Node> spillNodes = Selector.select(graph, stack, colors, backupNodes);
		
		// Replace colored temps
		final Map<Temp, Temp> colorMap = new HashMap<>();
		for (SimpleGraph<ColoredNode>.Node n : graph.nodeSet()) {
			if (n.info.isColored()) {
				colorMap.put(n.info.temp, n.info.color);
			}
		}
		
		for (int i = 0; i < assemFragment.body.size(); i++) {
		//for (Assem assem : assemFragment.body) {
			assemFragment.body.set(i, assemFragment.body.get(i).rename(new Function<Temp, Temp>() {
				
				@Override
				public Temp apply(Temp a) {
					Temp color = colorMap.get(a);
					return (color == null) ? a : color; 
				}
			}));
		}
		
		System.out.println(spillNodes);
		System.out.println(graph.nodeSet());
		System.out.println(colorMap);
		
		return graph;
	}
}
