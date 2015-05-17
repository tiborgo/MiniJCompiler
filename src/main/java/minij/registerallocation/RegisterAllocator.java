/*
 * MiniJCompiler: Compiler for a subset of the Java(R) language
 *
 * (C) Copyright 2014-2015 Tibor Goldschwendt <goldschwendt@cip.ifi.lmu.de>,
 * Michael Seifert <mseifert@error-reports.org>
 *
 * This file is part of MiniJCompiler.
 *
 * MiniJCompiler is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MiniJCompiler is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MiniJCompiler.  If not, see <http://www.gnu.org/licenses/>.
 */
package minij.registerallocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import minij.Configuration;
import minij.Logger;
import minij.flowanalysis.CoalesceableTemp;
import minij.flowanalysis.FlowAnalyser;
import minij.instructionselection.MachineSpecifics;
import minij.instructionselection.assems.Assem;
import minij.translate.layout.Fragment;
import minij.translate.layout.FragmentProc;
import minij.translate.layout.Frame;
import minij.translate.layout.Temp;
import minij.util.Function;
import minij.util.Pair;
import minij.util.SimpleGraph;

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
						boolean first = true;
						
						do {
							innerCounter++;

							// SIMPLIFY
							innerChanged = Simplifier.simplify(graph, stack, k);

							if (!config.noCoalesce) {
								// COALESCE
								if (first || innerChanged) {
									innerChanged = Coalescer.coalesce(config, graph, allocatedBody, k);
								}
							}
							else {
								innerChanged = false;
							}
							
							first = false;
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

					Assem renamedAssem = allocatedBody.get(i).rename(new Function<Temp, Temp>() {

						@Override
						public Temp apply(Temp a) {
							SimpleGraph<CoalesceableTemp> graph = finalGraph;
							SimpleGraph.Node<CoalesceableTemp> n = graph.get(new CoalesceableTemp(a));
							return (n.info.color == null) ? a : n.info.color;
						}
					});
					
					// remove moves where dst and src are equal
					Pair<Temp, Temp> move = renamedAssem.isMoveBetweenTemps();
					if (move != null && move.fst.equals(move.snd)) {
						allocatedBody.remove(i);
						i--;
					}
					else {
						allocatedBody.set(i, renamedAssem);
					}
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
