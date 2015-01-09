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

	public static SimpleGraph<ColoredTemp> allocate (SimpleGraph<Temp> interferenceGraph, FragmentProc<List<Assem>> assemFragment, MachineSpecifics machineSpecifics) {
		
		// slide 267
		
		List<Temp> colors = Arrays.asList(machineSpecifics.getGeneralPurposeRegisters());
		int k = colors.size();
		
		
		// BUILD
		final SimpleGraph<ColoredTemp> graph = Builder.build(interferenceGraph, colors);
		
		Map<ColoredTemp, SimpleGraph<ColoredTemp>.BackupNode> graphBackup = graph.backup();
		
		int coloredNodesCount = 0;
		for (SimpleGraph<ColoredTemp>.Node n : graph.nodeSet()) {
			if (n.info.isColored()) coloredNodesCount++;
		}
		
		List<ColoredTemp> stack = new LinkedList<>();

		do {
			// SIMPLIFY
			Simplifier.simplify(graph, stack, k);
			
			// SPILL
			Spiller.spill(graph, stack);
		}
		while(graph.nodeSet().size() > coloredNodesCount);
		
		// SELECT
		List<SimpleGraph<ColoredTemp>.Node> spillNodes = Selector.select(graph, stack, colors, graphBackup);
		
		
		
		
		// Replace colored temps
		for (int i = 0; i < assemFragment.body.size(); i++) {

			assemFragment.body.set(i, assemFragment.body.get(i).rename(new Function<Temp, Temp>() {
				
				@Override
				public Temp apply(Temp a) {
					SimpleGraph<ColoredTemp>.Node n = graph.get(new ColoredTemp(a));
					return (n == null || n.info.color == null) ? a : n.info.color; 
				}
			}));
		}
		
		System.out.println(spillNodes);
		System.out.println(graph.nodeSet());
		
		return graph;
	}
}
