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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import minij.Configuration;
import minij.Logger;
import minij.flowanalysis.CoalesceableTemp;
import minij.instructionselection.assems.Assem;
import minij.translate.layout.Temp;
import minij.util.Function;
import minij.util.Pair;
import minij.util.SimpleGraph;
import minij.util.SimpleGraph.Node;

public class Coalescer {

	static boolean coalesce(Configuration config, SimpleGraph<CoalesceableTemp> graph, List<Assem> allocatedBody, int k) {

		final Map<Temp, Temp> renames = new HashMap<>();
		boolean changed = false;

		// Slide 295

		//Set<Node<CoalesceableTemp>> nodes = new HashSet<>(graph.nodeSet());
		Map<Node<CoalesceableTemp>, Node<CoalesceableTemp>> nodes = new LinkedHashMap<>();
		
		for (Node<CoalesceableTemp> n : graph.nodeSet()) {
			nodes.put(n, n);
		}

		while(nodes.size() > 0) {

			boolean coalesceable = false;

			Node<CoalesceableTemp> a = nodes.values().iterator().next();

			for (Node<CoalesceableTemp> b : a.secondarySuccessors()) {


				// Cannot remove colored temp since registers can have a special purpose
				if (b.info.color == null) {



					Set<Node<CoalesceableTemp>> neighbours = new HashSet<>(a.neighbours());
					neighbours.addAll(b.neighbours());

					neighbours.remove(a);
					neighbours.remove(b);

					if (!coalesceable) {

						// BRIGGS

						int kNeighboursCount = 0;
						for (Node<CoalesceableTemp> n : neighbours) {
							if (n.degree() >= k) {
								kNeighboursCount++;
							}
						}
						if (kNeighboursCount < k) {
							coalesceable = true;
						}
					}

					
					if (!coalesceable) {
						
						// GEORGE
						
						coalesceable = true;
						for (Node<CoalesceableTemp> t : b.neighbours()) {
							if (!(t.degree() < k) && !graph.hasEdge(t, a)) {
								coalesceable = false;
								break;
							}
						}
					}

					// coalesce
					if (coalesceable) {
						changed = true;

						Node<CoalesceableTemp> ab = graph.merge(a, b, a.info);

						renames.put(b.info.temp, a.info.temp);
						
						// Remove move relations when the new node is interfering with one of its move neighbours
						for (Node<CoalesceableTemp> n : ab.secondaryNeighbours()) {
							if (graph.hasEdge(n, ab)) {
								graph.removeSecondaryEdge(n, ab);
								graph.removeSecondaryEdge(ab, n);
							}
						}
						
						nodes.remove(b);
						nodes.remove(a);
						nodes.put(ab, ab);

						break;
					}
				}
			}
			
			if (!coalesceable) {
				nodes.remove(a);
			}
		}


		if (renames.size() > 0) {
			for (int i = 0; i < allocatedBody.size(); i++) {

				Assem renameCandidate = allocatedBody.get(i);

				Set<Temp> temps = new HashSet<>(renameCandidate.use());
				temps.addAll(renameCandidate.def());

				if (!Collections.disjoint(temps, renames.keySet())) {


					Assem coalescedAssem = allocatedBody.get(i).rename(new Function<Temp, Temp>() {

						@Override
						public Temp apply(Temp t) {
							Temp newT = t;
							while (renames.get(newT) != null) {
								newT = renames.get(newT);
							}
							return newT;
						}
					});

					Pair<Temp, Temp> move = coalescedAssem.isMoveBetweenTemps();
					if (move != null && move.fst.equals(move.snd)) {
						allocatedBody.remove(i);
						i--;
					}
					else {
						allocatedBody.set(i, coalescedAssem);
					}
				}
			}
		}

		if (config.printCoalescingDetails) {
			Logger.log(graph.getName());
			Logger.log(renames.toString());
		}

		return changed;
	}
}
