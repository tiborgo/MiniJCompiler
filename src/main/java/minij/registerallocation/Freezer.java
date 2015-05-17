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
import minij.util.SimpleGraph;
import minij.util.SimpleGraph.Node;

public class Freezer {

	public static boolean freeze(SimpleGraph<CoalesceableTemp> graph, List<CoalesceableTemp> stack, int k) {

		for (SimpleGraph.Node<CoalesceableTemp> a : graph.nodeSet()) {
			if (a.degree() < k && a.secondaryNeighbours().size() > 0) {

				Set<SimpleGraph.Node<CoalesceableTemp>> Bs = a.secondaryNeighbours();
				for (Node<CoalesceableTemp> b : Bs) {					
					graph.removeSecondaryEdge(a, b);
					graph.removeSecondaryEdge(b, a);
					return true;
				}
			}
		}
		return false;
	}
}
