package minijava.backend.registerallocation;

import java.util.Arrays;
import java.util.List;

import minijava.backend.MachineSpecifics;
import minijava.intermediate.Temp;
import minijava.util.SimpleGraph;

public class Builder {

	public static SimpleGraph<ColoredNode> build (SimpleGraph<Temp> interferenceGraph, MachineSpecifics machineSpecifics) {
		
		SimpleGraph<ColoredNode> coloredInterferenceGraph = new SimpleGraph<>(interferenceGraph.getName());
		List<Temp> colors = Arrays.asList(machineSpecifics.getGeneralPurposeRegisters());

		for (SimpleGraph<Temp>.Node tNode : interferenceGraph.nodeSet()) {
			Temp color = null;
			if (colors.contains(tNode.info)) {
				color = tNode.info;
			}
			
			coloredInterferenceGraph.new Node(new ColoredNode(tNode.info, color));
		}
		
		return coloredInterferenceGraph;
	}
}
