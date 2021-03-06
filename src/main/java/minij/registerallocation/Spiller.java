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

import minij.flowanalysis.CoalesceableTemp;
import minij.util.SimpleGraph;

class Spiller {

	static boolean spill(SimpleGraph<CoalesceableTemp> graph, List<CoalesceableTemp> stack) {

		int maxDegree = 0;
		SimpleGraph.Node<CoalesceableTemp> maxDegreeNode = null;
		for (SimpleGraph.Node<CoalesceableTemp> n : graph.nodeSet()) {
			if (!n.info.isColored() && n.degree() > maxDegree) {
				maxDegree = n.degree();
				maxDegreeNode = n;
			}
		}

//		//if (maxDegreeNode != null) {
//		
//		/*for (SimpleGraph.Node<CoalesceableTemp> n : graph.nodeSet()) {
//			if (!n.info.isColored()/* && n.secondaryNeighbours().size() > 0 */) {
//				stack.add(n.info);
//				//graph.removeNode(n);
//				graph.deactivateNode(n);
//				return true;
//			}
//		}
//		return false;
		
		//SimpleGraph.Node<CoalesceableTemp> n = graph.nodeSet().iterator().next();
			

		//}
		
		if (maxDegreeNode != null) {
			graph.deactivateNode(maxDegreeNode);
			stack.add(maxDegreeNode.info);
			return true;
		}
		else {
			return false;
		}
	}
}
