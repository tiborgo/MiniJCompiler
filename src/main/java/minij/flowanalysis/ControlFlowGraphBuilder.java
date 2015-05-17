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
package minij.flowanalysis;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import minij.instructionselection.assems.Assem;
import minij.translate.layout.Label;
import minij.util.SimpleGraph;

class ControlFlowGraphBuilder {
	static SimpleGraph<Assem> build(List<Assem> body, String methodName) {

		SimpleGraph<Assem> graph = new SimpleGraph<>(methodName, true, true);
		List<SimpleGraph.Node<Assem>> jumps = new LinkedList<>();
		Map<Label, SimpleGraph.Node<Assem>> labelNodes = new HashMap<>();

		SimpleGraph.Node<Assem> previousNode = null;
		Iterator<Assem> iter = body.iterator();

		while (iter.hasNext()) {

			SimpleGraph.Node<Assem> currentNode = graph.addNode(iter.next());

			if (previousNode != null && previousNode.info.isFallThrough()) {
				graph.addEdge(previousNode, currentNode);
			}

			if (currentNode.info.jumps().size() > 0){
				jumps.add(currentNode);
			}

			if (currentNode.info.isLabel() != null) {
				labelNodes.put(currentNode.info.isLabel(), currentNode);
			}

			previousNode = currentNode;
		}

		for (SimpleGraph.Node<Assem> srcNode : jumps) {
			List<Label> dstLabels = srcNode.info.jumps();
			for (Label dstLabel : dstLabels) {
				SimpleGraph.Node<Assem> dstNode = labelNodes.get(dstLabel);
				graph.addEdge(srcNode, dstNode);
			}
		}

		return graph;
	}
}
