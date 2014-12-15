package minijava.backend.controlflowanalysis;

import java.util.List;

import minijava.backend.Assem;
import minijava.intermediate.Fragment;
import minijava.util.SimpleGraph;

public class ControlFlowGraphBuilder {
	public static SimpleGraph<Assem> buildControlFlowGraph(Fragment<List<Assem>> frag) {
		return new SimpleGraph<>();
	}
}
