package minij.flowanalysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import minij.instructionselection.assems.Assem;
import minij.translate.layout.Temp;
import minij.util.Pair;
import minij.util.SimpleGraph;

class InterferenceGraphBuilder {
	static SimpleGraph<CoalesceableTemp> build(SimpleGraph<Assem> controlFlowGraph,
			Map<Assem, LivenessSetsBuilder.InOut> inOut) {

		SimpleGraph<CoalesceableTemp> interferenceGraph = new SimpleGraph<>(controlFlowGraph.getName(), false, true);

		Map<Temp, SimpleGraph.Node<CoalesceableTemp>> nodes = new HashMap<>();
		
		for (SimpleGraph.Node<Assem> n : controlFlowGraph.nodeSet()) {
			
			List<Temp> ts = new ArrayList<>(n.info.use());
			ts.addAll(n.info.def());
		
			for (Temp t : ts) {
				
				SimpleGraph.Node<CoalesceableTemp> node = nodes.get(t);
				
				if (node == null) {
					node = interferenceGraph.addNode(new CoalesceableTemp(t, null));
					nodes.put(t, node);
				}
				
				if (n.info.isMoveBetweenTemps() != null) {
					node.info.addMove(n.info);
				}
			}
		}

		for (SimpleGraph.Node<Assem> n : controlFlowGraph.nodeSet()) {

			Pair<Temp, Temp> moveInstruction = n.info.isMoveBetweenTemps();

			if (moveInstruction == null) {
				for (Temp t : n.info.def()) {

					for (Temp u : inOut.get(n.info).out) {
						if (!u.equals(t)) {
							SimpleGraph.Node<CoalesceableTemp> tNode = nodes.get(t);
							SimpleGraph.Node<CoalesceableTemp> uNode = nodes.get(u);
							if (tNode == null) {
								tNode = interferenceGraph.addNode(new CoalesceableTemp(t, null));
								nodes.put(t, tNode);
							}
							if (uNode == null) {
								throw new RuntimeException("Do not know temp '" + u + "'");
							}
							if (!tNode.predecessors().contains(uNode)) {
								interferenceGraph.addEdge(tNode, uNode);
							}
						}
					}
				}
			}
			else {

				for (Temp u : inOut.get(n.info).out) {

					if (!u.equals(moveInstruction.snd) && !u.equals(moveInstruction.fst)) {
						SimpleGraph.Node<CoalesceableTemp> tNode = nodes.get(moveInstruction.fst);
						SimpleGraph.Node<CoalesceableTemp> uNode = nodes.get(u);
						if (tNode == null) {
							tNode = interferenceGraph.addNode(new CoalesceableTemp(moveInstruction.fst, null));
							nodes.put(moveInstruction.fst, tNode);
						}
						if (uNode == null) {
							throw new RuntimeException("Do not know temp '" + u + "'");
						}
						if (!tNode.predecessors().contains(uNode)) {
							interferenceGraph.addEdge(tNode, uNode);
						}
					}
				}
			}
		}

		return interferenceGraph;
	}
}
