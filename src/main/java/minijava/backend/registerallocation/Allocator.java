package minijava.backend.registerallocation;

import java.util.Arrays;
import java.util.Collections;
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

	public static FragmentProc<List<Assem>> allocate (FragmentProc<List<Assem>> frag, MachineSpecifics machineSpecifics) {
		
		// slide 267
		
		List<Temp> colors = Arrays.asList(machineSpecifics.getGeneralPurposeRegisters());
		int k = colors.size();
		List<Temp> spillNodes = Collections.emptyList();
		FragmentProc<List<Assem>> allocatedFrag = new FragmentProc<>(frag.frame, frag.body);
		
		int counter = 0;
		SimpleGraph<ColoredTemp> graph;
		
		do {
			System.out.println("#################");
			System.out.println(frag.frame.getName());
			
			// BUILD
			graph = Builder.build(colors, allocatedFrag);
			
			if (counter > 2) {
				//break;
			}
			
			counter++;
			
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
			spillNodes = Selector.select(graph, stack, colors, graphBackup);
			
			// rewrite program
			allocatedFrag = new FragmentProc<>(allocatedFrag.frame, machineSpecifics.spill(allocatedFrag.frame, allocatedFrag.body, spillNodes));
			
			System.out.println("Register allocator round " + counter + ", " + spillNodes.size() + " spill nodes " + spillNodes);
			//System.out.println(machineSpecifics.printAssembly(Arrays.<Fragment<List<Assem>>>asList(allocatedFrag)));
			
			// START OVER
			
		}
		while(spillNodes.size() > 0);
		
		final SimpleGraph<ColoredTemp> finalGraph = graph;
		
		// Replace colored temps
		for (int i = 0; i < allocatedFrag.body.size(); i++) {

			allocatedFrag.body.set(i, allocatedFrag.body.get(i).rename(new Function<Temp, Temp>() {
				
				@Override
				public Temp apply(Temp a) {
					SimpleGraph<ColoredTemp>.Node n = finalGraph.get(new ColoredTemp(a));
					return (n.info.color == null) ? a : n.info.color; 
				}
			}));
		}
		
		return allocatedFrag;
	}
}
