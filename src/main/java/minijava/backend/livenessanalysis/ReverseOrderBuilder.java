package minijava.backend.livenessanalysis;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import minijava.backend.Assem;
import minijava.util.SimpleGraph;

public class ReverseOrderBuilder {
	
	public static List<SimpleGraph<Assem>.Node> build(SimpleGraph<Assem> graph) {
		
		// Build reverse order
		
		List<SimpleGraph<Assem>.Node> nodes = new ArrayList<>(graph.nodeSet().size());
		List<SimpleGraph<Assem>.Node> remainingNodes = new LinkedList<>(graph.nodeSet());
		
		Function<SimpleGraph<Assem>.Node, List<SimpleGraph<Assem>.Node>> orderReversely = new Function<SimpleGraph<Assem>.Node, List<SimpleGraph<Assem>.Node>>() {
			
			@Override
			public List<SimpleGraph<Assem>.Node> apply(SimpleGraph<Assem>.Node node) {
				List<SimpleGraph<Assem>.Node> nodes = new LinkedList<>();
		    	nodes.add(node);
		    	remainingNodes.remove(node);
		    	
		    	for (SimpleGraph<Assem>.Node pre : node.predecessors()) {
		    		if (remainingNodes.contains(pre)) {
		    			nodes.addAll(this.apply(pre));
		    		}
		    	}
		    	
		    	return nodes;
			}
		};
		
		// Find a node with no successors because its propably the last instruction
		
		Function<List<SimpleGraph<Assem>.Node>, SimpleGraph<Assem>.Node> findSink = new Function<List<SimpleGraph<Assem>.Node>, SimpleGraph<Assem>.Node>() {

			@Override
			public SimpleGraph<Assem>.Node apply(List<SimpleGraph<Assem>.Node> nodes) {
				SimpleGraph<Assem>.Node sink = null;
				for (SimpleGraph<Assem>.Node n : nodes) {
					if (n.successors().size() == 0) {
						sink = n;
						break;
					}
				}
				return (sink == null) ? nodes.get(nodes.size()-1) : sink; 
			}
		};
		
		
		
		while(remainingNodes.size() > 0) {
			SimpleGraph<Assem>.Node sink = findSink.apply(remainingNodes);
			nodes.addAll(orderReversely.apply(sink));
		}
		
		return nodes;
	}
}
