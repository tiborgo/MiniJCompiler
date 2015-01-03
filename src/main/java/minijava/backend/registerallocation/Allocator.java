package minijava.backend.registerallocation;

import minijava.backend.MachineSpecifics;
import minijava.intermediate.Temp;
import minijava.util.SimpleGraph;

public class Allocator {
	

	public static SimpleGraph<ColoredNode> allocate (SimpleGraph<Temp> interferenceGraph, MachineSpecifics machineSpecifics) {
		
		// BUILD
		SimpleGraph<ColoredNode> coloredInterferenceGraph = Builder.build(interferenceGraph, machineSpecifics);

		// SIMPLIFY
		
		return coloredInterferenceGraph;
	}
}
