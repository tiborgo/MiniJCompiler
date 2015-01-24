package minijava.registerallocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

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

import minijava.util.GraphSaver;

public class RegisterAllocator {

	public static List<Fragment<List<Assem>>> allocateRegisters(Configuration config, List<Fragment<List<Assem>>> frags, MachineSpecifics machineSpecifics) throws RegisterAllocatorException {

		try {
			int totalRounds = 0;

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


					List<CoalesceableTemp> stack = new LinkedList<>();

					boolean outerChanged = false;
					int innerCounter = 0;
					do {
						boolean innerChanged;

						do {
							innerCounter++;

							// SIMPLIFY
							innerChanged = Simplifier.simplify(graph, stack, k);

							// COALESCE						
							innerChanged = Coalescer.coalesce(config, graph, allocatedBody, k) || innerChanged;
						}
						while (innerChanged);

						// FREEZE
						outerChanged = Freezer.freeze(graph, stack, k);

						// SPILL
						outerChanged = outerChanged ||  Spiller.spill(graph, stack);
					}
					while(outerChanged);
					
					if (config.printCoalescingDetails) {
						Logger.log(innerCounter + " coalescing rounds");
					}

					// SELECT
					spillNodes = Selector.select(graph, stack, colors/*, graphBackup*/);

					// rewrite program
					allocatedBody = machineSpecifics.spill(allocatedFrame, allocatedBody, spillNodes);

					if (config.printRegisterAllocationDetails) {
						Logger.log("Register allocator round " + counter + ", " + spillNodes.size() + " spill nodes " + spillNodes);
					}

					// START OVER
				}
				while(spillNodes.size() > 0);

				totalRounds += counter;

				final SimpleGraph<CoalesceableTemp> finalGraph = graph;

				// Replace colored temps
				for (int i = 0; i < allocatedBody.size(); i++) {

					allocatedBody.set(i, allocatedBody.get(i).rename(new Function<Temp, Temp>() {

						@Override
						public Temp apply(Temp a) {
							SimpleGraph<CoalesceableTemp> graph = finalGraph;
							SimpleGraph.Node<CoalesceableTemp> n = graph.get(new CoalesceableTemp(a));
							return (n.info.color == null) ? a : n.info.color;
						}
					}));
				}

				allocatedFrags.add(new FragmentProc<>(allocatedFrame, allocatedBody));
			}

			Logger.logVerbosely("Successfully allocated registers (" + totalRounds + " round/s)");

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
