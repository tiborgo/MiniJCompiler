package minijava.backend.controlflowanalysis;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import minijava.backend.Assem;
import minijava.util.Pair;
import minijava.util.SimpleGraph;

public class ActivityGraphBuilder {

	public static Pair<Map<Assem, Set<SimpleGraph<Assem>.Node>>, Map<Assem, Set<SimpleGraph<Assem>.Node>>> buildActivityGraph(
			SimpleGraph<Assem> controlFlowGraph) {
		return new Pair<>(Collections.<Assem, Set<SimpleGraph<Assem>.Node>>emptyMap(),
				Collections.<Assem, Set<SimpleGraph<Assem>.Node>>emptyMap());
	}
}
