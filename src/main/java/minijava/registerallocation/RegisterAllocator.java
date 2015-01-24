package minijava.registerallocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import minijava.Configuration;
import minijava.Logger;
import minijava.flowanalysis.CoalesceableTemp;
import minijava.flowanalysis.FlowAnalyser;
import minijava.instructionselection.MachineSpecifics;
import minijava.instructionselection.assems.Assem;
import minijava.translate.layout.Fragment;
import minijava.translate.layout.FragmentProc;
import minijava.translate.layout.Frame;
import minijava.translate.layout.Temp;
import minijava.util.Function;
import minijava.util.SimpleGraph;
import minijava.util.SimpleGraph.Node;

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
				Frame allocatedFrame = fragProc.frame;
				List<Assem> allocatedBody = new ArrayList<>(fragProc.body);
				int counter = 0;
				SimpleGraph<CoalesceableTemp> graph;
		
				do {
					counter++;
					
					if (config.printRegisterAllocationDetails) {
						Logger.log("#################");
						Logger.log(fragProc.frame.getName().toString());
					}
		
					graph = FlowAnalyser.analyseFlow(config, allocatedBody, allocatedFrame.getName().toString());
					
					// BUILD
					Builder.build(graph, colors);
		
					//System.out.println(graph.getDot());
		
					//Map<CoalesceableTemp, SimpleGraph.BackupNode<CoalesceableTemp>> graphBackup = graph.backup();
		
		
					List<CoalesceableTemp> stack = new LinkedList<>();
		
					int coloredNodesCount = 0;
					boolean spilled;
					boolean freezed;
					do {
						int insignificantNodesCount = 0;
						//do {
							boolean changed;
							do {
								// SIMPLIFY
								changed = Simplifier.simplify(graph, stack, k);
							
								// COALESCE
								
								changed = Coalescer.coalesce(graph, allocatedBody, k, allocatedFrame.getName().toString()) || changed;
								
								//System.out.println(graph.getDot());
							}
							while (changed);
							
							// FREEZE
							freezed = Freezer.freeze(graph, stack, k);
							
							/*for (Node<CoalesceableTemp> n : graph.nodeSet()) {
								if (n.degree() < k) {
									insignificantNodesCount++;
								}
							}*/
							
							
							if (!freezed/* && graph.nodeSet().size() > 0*/) {
								// SPILL
								spilled = Spiller.spill(graph, stack);
							}
							else {
								spilled = false;
							}
						//}
						//while(insignificantNodesCount > 0);
		
						
						
						/*for (SimpleGraph.Node<CoalesceableTemp> n : graph.nodeSet()) {
							if (n.info.isColored()) {
								coloredNodesCount++;
							}
						}*/
					}
					while(freezed || spilled/*graph.nodeSet().size() > coloredNodesCount*/);
					
					//System.out.println(machineSpecifics.printAssembly(Arrays.<Fragment<List<Assem>>>asList(new FragmentProc<List<Assem>>(allocatedFrame, allocatedBody))));
		
					// SELECT
					spillNodes = Selector.select(graph, stack, colors/*, graphBackup*/);
		
					// rewrite program
					allocatedBody = machineSpecifics.spill(allocatedFrame, allocatedBody, spillNodes);
		
					if (config.printRegisterAllocationDetails) {
						Logger.log("Register allocator round " + counter + ", " + spillNodes.size() + " spill nodes " + spillNodes);
						//System.out.println(machineSpecifics.printAssembly(Arrays.<Fragment<List<Assem>>>asList(allocatedFrag)));+
					}
		
					// START OVER
		
				}
				while(spillNodes.size() > 0);
		
				final SimpleGraph<CoalesceableTemp> finalGraph = graph;
		
				// Replace colored temps
				for (int i = 0; i < allocatedBody.size(); i++) {
		
					allocatedBody.set(i, allocatedBody.get(i).rename(new Function<Temp, Temp>() {
		
						@Override
						public Temp apply(Temp a) {
							SimpleGraph.Node<CoalesceableTemp> n = finalGraph.get(new CoalesceableTemp(a));
							return (n.info.color == null) ? a : n.info.color;
						}
					}));
				}
		
				allocatedFrags.add(new FragmentProc<>(allocatedFrame, allocatedBody));
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
