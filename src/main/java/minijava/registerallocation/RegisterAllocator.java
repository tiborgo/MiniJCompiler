package minijava.registerallocation;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import minijava.Configuration;
import minijava.Logger;
import minijava.flowanalysis.FlowAnalyser;
import minijava.instructionselection.MachineSpecifics;
import minijava.instructionselection.assems.Assem;
import minijava.translate.layout.Fragment;
import minijava.translate.layout.FragmentProc;
import minijava.translate.layout.Temp;
import minijava.util.Function;
import minijava.util.SimpleGraph;

public class RegisterAllocator {

	public static List<Fragment<List<Assem>>> allocateRegisters(Configuration config, List<Fragment<List<Assem>>> frags, MachineSpecifics machineSpecifics) throws RegisterAllocatorException {
		
		try {
			List<Fragment<List<Assem>>> allocatedFrags = new LinkedList<>();
			
			for (Fragment<List<Assem>> frag : frags) {
				
				FragmentProc<List<Assem>> fragProc = (FragmentProc<List<Assem>>) frag;
								
				// slide 267
				
				List<Temp> colors = Arrays.asList(machineSpecifics.getGeneralPurposeRegisters());
				int k = colors.size();
				List<Temp> spillNodes = Collections.emptyList();
				FragmentProc<List<Assem>> allocatedFrag = new FragmentProc<>(fragProc.frame, fragProc.body);
		
				int counter = 0;
				SimpleGraph<ColoredTemp> graph;
		
				do {
					if (config.printRegisterAllocationDetails) {
						Logger.log("#################");
						Logger.log(fragProc.frame.getName().toString());
					}
		
					SimpleGraph<Temp> interferenceGraph = FlowAnalyser.analyseFlow(config, allocatedFrag);
					
					// BUILD
					graph = Builder.build(interferenceGraph, colors);
		
					if (counter > 2) {
						//break;
					}
		
					counter++;
		
					Map<ColoredTemp, SimpleGraph.BackupNode<ColoredTemp>> graphBackup = graph.backup();
		
					int coloredNodesCount = 0;
					for (SimpleGraph.Node<ColoredTemp> n : graph.nodeSet()) {
						if (n.info.isColored()) {
							coloredNodesCount++;
						}
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
		
					if (config.printRegisterAllocationDetails) {
						Logger.log("Register allocator round " + counter + ", " + spillNodes.size() + " spill nodes " + spillNodes);
						//System.out.println(machineSpecifics.printAssembly(Arrays.<Fragment<List<Assem>>>asList(allocatedFrag)));+
					}
		
					// START OVER
		
				}
				while(spillNodes.size() > 0);
		
				final SimpleGraph<ColoredTemp> finalGraph = graph;
		
				// Replace colored temps
				for (int i = 0; i < allocatedFrag.body.size(); i++) {
		
					allocatedFrag.body.set(i, allocatedFrag.body.get(i).rename(new Function<Temp, Temp>() {
		
						@Override
						public Temp apply(Temp a) {
							SimpleGraph.Node<ColoredTemp> n = finalGraph.get(new ColoredTemp(a));
							return (n.info.color == null) ? a : n.info.color;
						}
					}));
				}
		
				allocatedFrags.add(allocatedFrag);
			}
			
			Logger.logVerbosely("Successfully allocated registers");
			
			return allocatedFrags;
		}
		catch (ClassCastException e) {
			throw new RegisterAllocatorException("Can only alocate registers for FragementProc", e);
		}
		catch (Exception e) {
			throw new RegisterAllocatorException("Failed to allocate registers", e);
		}
	}
}
