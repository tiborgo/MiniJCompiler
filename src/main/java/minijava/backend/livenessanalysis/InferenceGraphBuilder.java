package minijava.backend.livenessanalysis;

import java.util.Map;
import java.util.Set;

import minijava.backend.Assem;
import minijava.intermediate.Temp;
import minijava.util.SimpleGraph;

public class InferenceGraphBuilder {
	public static SimpleGraph<Temp> build(SimpleGraph<Assem> controlFlowGraph, 
			Map<Assem, Set<Temp>> in, Map<Assem, Set<Temp>> out) {
		// TODO: implement
		return new SimpleGraph<>();
	}
}
