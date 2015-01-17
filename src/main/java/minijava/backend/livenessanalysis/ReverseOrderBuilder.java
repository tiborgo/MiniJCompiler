package minijava.backend.livenessanalysis;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import minijava.backend.Assem;
import minijava.util.Function;
import minijava.util.SimpleGraph;

public class ReverseOrderBuilder {

	public static List<SimpleGraph.Node<Assem>> build(SimpleGraph<Assem> graph) {

		// Build reverse order

		List<SimpleGraph.Node<Assem>> nodes = new ArrayList<>(graph.nodeSet().size());
		final List<SimpleGraph.Node<Assem>> remainingNodes = new LinkedList<>(graph.nodeSet());

		Function<SimpleGraph.Node<Assem>, List<SimpleGraph.Node<Assem>>> orderReversely = new Function<SimpleGraph.Node<Assem>, List<SimpleGraph.Node<Assem>>>() {

			@Override
			public List<SimpleGraph.Node<Assem>> apply(SimpleGraph.Node<Assem> node) {
				List<SimpleGraph.Node<Assem>> nodes = new LinkedList<>();
		    	nodes.add(node);
		    	remainingNodes.remove(node);

		    	for (SimpleGraph.Node<Assem> pre : node.predecessors()) {
		    		if (remainingNodes.contains(pre)) {
		    			nodes.addAll(this.apply(pre));
		    		}
		    	}

		    	return nodes;
			}
		};

		// Find a node with no successors because its propably the last instruction

		Function<List<SimpleGraph.Node<Assem>>, SimpleGraph.Node<Assem>> findSink = new Function<List<SimpleGraph.Node<Assem>>, SimpleGraph.Node<Assem>>() {

			@Override
			public SimpleGraph.Node<Assem> apply(List<SimpleGraph.Node<Assem>> nodes) {
				SimpleGraph.Node<Assem> sink = null;
				for (SimpleGraph.Node<Assem> n : nodes) {
					if (n.successors().size() == 0) {
						sink = n;
						break;
					}
				}
				return (sink == null) ? nodes.get(nodes.size()-1) : sink;
			}
		};



		while(remainingNodes.size() > 0) {
			SimpleGraph.Node<Assem> sink = findSink.apply(remainingNodes);
			nodes.addAll(orderReversely.apply(sink));
		}

		return nodes;
	}
}
