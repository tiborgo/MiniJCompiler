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
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import minij.flowanalysis.CoalesceableTemp;
import minij.translate.layout.Temp;
import minij.util.SimpleGraph;

class Selector {

	static List<Temp> select(
			SimpleGraph<CoalesceableTemp> graph,
			List<CoalesceableTemp> stack,
			List<Temp> colors) {

		List<Temp> spilledNodes = new LinkedList<>();
		Collections.reverse(stack);

		for (CoalesceableTemp t : stack) {

			SimpleGraph.Node<CoalesceableTemp> n = graph.get(t);
			graph.activateNode(n);

			// Find color for node
			Set<SimpleGraph.Node<CoalesceableTemp>> neighbours = n.neighbours();

			for (Temp color : colors) {
				boolean occupied = false;
				for (SimpleGraph.Node<CoalesceableTemp> neighbour : neighbours) {
					if (color.equals(neighbour.info.color)) {
						occupied = true;
						break;
					}
				}
				if (!occupied) {
					n.info.color = color;
					break;
				}
			}

			if (n.info.color == null) {
				spilledNodes.add(n.info.temp);
			}
		}

		stack.clear();

		return spilledNodes;
	}
}
