package minijava.flowanalysis;

import java.util.HashMap;
import java.util.Map;

import minijava.instructionselection.assems.Assem;
import minijava.translate.layout.Temp;
import minijava.util.Pair;
import minijava.util.SimpleGraph;

class InterferenceGraphBuilder {
	static SimpleGraph<CoalesceableTemp> build(SimpleGraph<Assem> controlFlowGraph,
			Map<Assem, LivenessSetsBuilder.InOut> inOut) {

		SimpleGraph<CoalesceableTemp> interferenceGraph = new SimpleGraph<>(controlFlowGraph.getName());

		Map<Temp, SimpleGraph.Node<CoalesceableTemp>> nodes = new HashMap<>();
		
		for (SimpleGraph.Node<Assem> n : controlFlowGraph.nodeSet()) {
		
			for (Temp t : inOut.get(n.info).in) {
				
				SimpleGraph.Node<CoalesceableTemp> node = nodes.get(t);
				
				if (node == null) {
					node = interferenceGraph.addNode(new CoalesceableTemp(t, null, false));
					nodes.put(t, node);
				}
				
				node.info.moveRelated = node.info.moveRelated && (n.info.isMoveBetweenTemps() != null); 
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
								tNode = interferenceGraph.addNode(new CoalesceableTemp(t, null, false));
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
							tNode = interferenceGraph.addNode(new CoalesceableTemp(moveInstruction.fst, null, false));
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
