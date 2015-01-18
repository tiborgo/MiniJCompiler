package minijava.backend.livenessanalysis;

import java.util.HashMap;
import java.util.Map;

import minijava.backend.Assem;
import minijava.translate.layout.Temp;
import minijava.util.Pair;
import minijava.util.SimpleGraph;

public class InterferenceGraphBuilder {
	public static SimpleGraph<Temp> build(SimpleGraph<Assem> controlFlowGraph,
			Map<Assem, LivenessSetsBuilder.InOut> inOut) {

		SimpleGraph<Temp> interferenceGraph = new SimpleGraph<>(controlFlowGraph.getName());

		Map<Temp, SimpleGraph.Node<Temp>> nodes = new HashMap<>();
		for (LivenessSetsBuilder.InOut inOutN : inOut.values()) {
			for (Temp t : inOutN.in) {
				if (nodes.get(t) == null) {
					SimpleGraph.Node<Temp> node = interferenceGraph.addNode(t);
					nodes.put(t, node);
				}
			}
		}

		for (SimpleGraph.Node<Assem> n : controlFlowGraph.nodeSet()) {

			Pair<Temp, Temp> moveInstruction = n.info.isMoveBetweenTemps();

			if (moveInstruction == null) {
				for (Temp t : n.info.def()) {

					for (Temp u : inOut.get(n.info).out) {
						if (!u.equals(t)) {
							SimpleGraph.Node<Temp> tNode = nodes.get(t);
							SimpleGraph.Node<Temp> uNode = nodes.get(u);
							if (tNode == null) {
								tNode = interferenceGraph.addNode(t);
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
						SimpleGraph.Node<Temp> tNode = nodes.get(moveInstruction.fst);
						SimpleGraph.Node<Temp> uNode = nodes.get(u);
						if (tNode == null) {
							tNode = interferenceGraph.addNode(moveInstruction.fst);
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
