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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import minij.flowanalysis.CoalesceableTemp;
import minij.util.SimpleGraph;

class Simplifier {

	static boolean simplify(SimpleGraph<CoalesceableTemp> graph, List<CoalesceableTemp> stack, int k) {

		boolean changed = false;
		boolean loopChanged ;
		
		do {
			
			loopChanged = false;
			
			Set<SimpleGraph.Node<CoalesceableTemp>> nodes = new HashSet<>(graph.nodeSet());
	
			for (SimpleGraph.Node<CoalesceableTemp> node : nodes) {
	
				if (node.secondaryNeighbours().size() == 0 && node.degree() < k && !node.info.isColored()) {
					stack.add(node.info);
					graph.deactivateNode(node);
					changed = true;
					loopChanged = true;
				}
			}
		}
		while(loopChanged);
		
		return changed;
	}
}
