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

import java.util.List;
import java.util.Set;

import minij.flowanalysis.CoalesceableTemp;
import minij.flowanalysis.FlowAnalyserException;
import minij.translate.layout.Temp;
import minij.util.Pair;
import minij.util.SimpleGraph;

class Builder {

	static void build(SimpleGraph<CoalesceableTemp> interferenceGraph, List<Temp> colors) throws FlowAnalyserException {

		// Add secondary edges between temps that are connected through a move
		for (SimpleGraph.Node<CoalesceableTemp> a : interferenceGraph.nodeSet()) {
			for (Temp tB : a.info.getPartners()) {
				SimpleGraph.Node<CoalesceableTemp> b = interferenceGraph.get(new CoalesceableTemp(tB));
				
				Set<Pair<Temp, Temp>> moveTempsSet = a.info.getMoveTemps(b.info.temp);
				
				for (Pair<Temp, Temp> moveTemps: moveTempsSet) {
					
					if (moveTemps.fst.equals(a.info.temp)) {
						// dont include duplicate edges or edges between interfering nodes, because these nodes cannot be coalesced anyway
						if (!interferenceGraph.hasEdge(a, b) && !interferenceGraph.hasSecondaryEdge(b, a)) {
							interferenceGraph.addSecondaryEdge(b, a);
						}
					}
					else {
						// dont include duplicate edges or edges between interfering nodes, because these nodes cannot be coalesced anyway
						if (!interferenceGraph.hasEdge(a, b) && !interferenceGraph.hasSecondaryEdge(a, b)) {
							interferenceGraph.addSecondaryEdge(a, b);
						}
					}
				}
				
				
			}
		}
		
		for (SimpleGraph.Node<CoalesceableTemp> tNode : interferenceGraph.nodeSet()) {
			if (colors.contains(tNode.info.temp)) {
				tNode.info.color = tNode.info.temp;
			}
		}
	}
}
