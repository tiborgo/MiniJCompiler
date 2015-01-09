package minijava.backend.registerallocation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import minijava.backend.Assem;
import minijava.backend.MachineSpecifics;
import minijava.intermediate.FragmentProc;
import minijava.intermediate.Temp;
import minijava.util.Function;
import minijava.util.SimpleGraph;

public class Allocator {

	public static SimpleGraph<ColoredNode> allocate (SimpleGraph<Temp> interferenceGraph, FragmentProc<List<Assem>> assemFragment, MachineSpecifics machineSpecifics) {
		
		// slide 267
		
		List<Temp> colors = Arrays.asList(machineSpecifics.getGeneralPurposeRegisters());
		int k = colors.size();
		
		
		// BUILD
		SimpleGraph<ColoredNode> graph = Builder.build(interferenceGraph, colors);
		
		Map<Temp, SimpleGraph<ColoredNode>.BackupNode> backupNodes = new HashMap<>();
		for (SimpleGraph<ColoredNode>.Node n : graph.nodeSet()) {
			backupNodes.put(n.info.temp, n.backup());
		}
		
		int coloredNodesCount = 0;
		for (SimpleGraph<ColoredNode>.Node n : graph.nodeSet()) {
			if (n.info.isColored()) coloredNodesCount++;
		}
		
		List<Temp> stack = new LinkedList<>();

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
