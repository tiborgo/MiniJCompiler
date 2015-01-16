package minijava.backend.livenessanalysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import minijava.backend.Assem;
import minijava.intermediate.Temp;
import minijava.util.SimpleGraph;

public class LivenessSetsBuilder {
	
	static public class InOut {
		public final Set<Temp> in = new HashSet<>();
		public final Set<Temp> out = new HashSet<>();
		
		@Override
		public boolean equals(Object obj) {
			return (obj instanceof InOut && in.equals(((InOut)obj).in) && out.equals(((InOut)obj).out));
		}
		
		@Override
		public String toString() {
			return "in: " + in.toString() + ",\t\tout: " + out.toString();
		}
	}
	
	public static Map<Assem, InOut> build(SimpleGraph<Assem> controlFlowGraph, List<Assem> assems) {
		
		//final List<SimpleGraph<Assem>.Node> nodes = ReverseOrderBuilder.build(controlFlowGraph);
		List<Assem> reverseAssems = new ArrayList<>(assems);
		Collections.reverse(reverseAssems);
		
		boolean changed;
		
		// With the help of the comparator the Map keys keep the order of the program flow.
		// Helps a lot when debugging
		/*Comparator<Assem> comparator = new Comparator<Assem>() {

			@Override
			public int compare(Assem o1, Assem o2) {
				int iO1 = 0;
				int iO2 = 0;
				int found = 0;
				
				for (int i = 0; i < nodes.size(); i++) {
					SimpleGraph<Assem>.Node n = nodes.get(i);
					if (n.info.equals(o1)) {
						iO1 = i;
						found++;
					}
					if (n.info.equals(o2)) {
						iO2 = i;
						found++;
					}
					if (found == 2) {
						break;
					}
				}
				return iO2 - iO1;
			}
		};*/
		
		Map<Assem, InOut> inOut = new HashMap<>();//TreeMap<>(comparator);
		
		for (Assem assem : reverseAssems) {
		//for (int i = 0; i < nodes.size(); i++) {
			inOut.put(assem, new InOut());
		}
		
		int printCountEach = 500;
		int counter = 0;
		
		do {
			
			if (counter % printCountEach == printCountEach-1) {
				System.out.println("Liveness Sets Builder round: " + (counter+1));
			}
			counter++;
		
			changed = false;

			for (Assem a : reverseAssems) {
				
				SimpleGraph<Assem>.Node n = controlFlowGraph.get(a);
				
				// old in and out set
				InOut inOutN_ = inOut.get(n.info);
				// new in and out set
				InOut inOutN = new InOut();
				
				// OUT
				for (SimpleGraph<Assem>.Node s : n.successors()) {
					Set<Temp> inS = inOut.get(s.info).in;
					inOutN.out.addAll(inS);
				}
				
				// IN
				inOutN.in.addAll(inOutN.out);
				List<Temp> def = n.info.def();
				inOutN.in.removeAll(def);
				List<Temp> use = n.info.use();
				inOutN.in.addAll(use);

				// replace if changed
				if (!inOutN.equals(inOutN_)) {
					inOut.put(n.info, inOutN);
					changed = true;
				}
			}
		}
		while (changed);
		
		return inOut;
	}
}
