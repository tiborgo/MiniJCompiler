package minijava.backend.controlflowanalysis;

import java.util.Map;
import java.util.Set;

import minijava.backend.Assem;
import minijava.util.SimpleGraph;

public class InferenceGraphBuilder {
	public static SimpleGraph<Assem> buildInferenceGraph(SimpleGraph<Assem> controlFlowGraph, 
			Map<Assem, Set<SimpleGraph<Assem>.Node>> in, Map<Assem, Set<SimpleGraph<Assem>.Node>> out) {
		return new SimpleGraph<>();
	}
}
