package minijava.backend.livenessanalysis;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import minijava.backend.Assem;
import minijava.intermediate.Temp;
import minijava.util.Pair;
import minijava.util.SimpleGraph;

public class InterferenceGraphBuilder {
	public static SimpleGraph<Temp> build(SimpleGraph<Assem> controlFlowGraph, 
			Map<Assem, Set<Temp>> in, Map<Assem, Set<Temp>> out) {

		SimpleGraph<Temp> interferenceGraph = new SimpleGraph<>(controlFlowGraph.getName());
		
		Map<Temp, SimpleGraph<Temp>.Node> nodes = new HashMap<>();
		for (Set<Temp> ts : in.values()) {
			for (Temp t : ts) {
				if (nodes.get(t) == null) {
					SimpleGraph<Temp>.Node node = interferenceGraph.new Node(t);
					nodes.put(t, node);
				}
			}
		}
		
		for (SimpleGraph<Assem>.Node n : controlFlowGraph.nodeSet()) {
			
			Pair<Temp, Temp> moveInstruction = n.info.isMoveBetweenTemps();
			
			if (moveInstruction == null) {
				for (Temp t : n.info.def()) {
					
					for (Temp u : out.get(n.info)) {
						if (!u.equals(t)) {
							SimpleGraph<Temp>.Node tNode = nodes.get(t);
							SimpleGraph<Temp>.Node uNode = nodes.get(u);
							if (tNode == null) {
								tNode = interferenceGraph.new Node(t);
								nodes.put(t, tNode);
							}
							if (uNode == null) {
								throw new RuntimeException("Do not know temp '" + u + "'");
							}
							interferenceGraph.addEdge(tNode, uNode);
						}
					}
				}
			}
			else {
				
				for (Temp u : out.get(n.info)) {
					
					if (!u.equals(moveInstruction.snd) && !u.equals(moveInstruction.fst)) {
						SimpleGraph<Temp>.Node tNode = nodes.get(moveInstruction.fst);
						SimpleGraph<Temp>.Node uNode = nodes.get(u);
						if (uNode == null) {
							throw new RuntimeException("Do not know temp '" + u + "'");
						}
						interferenceGraph.addEdge(tNode, uNode);
					}
				}
			}
		}
		
		return interferenceGraph;
	}
}
