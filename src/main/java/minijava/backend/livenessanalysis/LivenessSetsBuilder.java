package minijava.backend.livenessanalysis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import minijava.backend.Assem;
import minijava.intermediate.Temp;
import minijava.util.Pair;
import minijava.util.SimpleGraph;

public class LivenessSetsBuilder {

	public static Pair<Map<Assem, Set<Temp>>, Map<Assem, Set<Temp>>> build(
			SimpleGraph<Assem> controlFlowGraph) {
		
		Map<Assem, Set<Temp>> in = new HashMap<>();
		Map<Assem, Set<Temp>> out = new HashMap<>();
		
		boolean changed;
		
		do {
		
			changed = false;
			
			for (SimpleGraph<Assem>.Node n : controlFlowGraph.nodeSet()) {
				
				// OUT
				Set<Temp> outN = new HashSet<>();
				
				for (SimpleGraph<Assem>.Node s : n.successors()) {
					Set<Temp> inS = in.get(s);
					if (inS != null) {
						outN.addAll(inS);
					}
				}
				
				if (!outN.equals(out.get(n.info))) {
					changed = true;
				}
				out.put(n.info, outN);
				
				// IN
				Set<Temp> inN = new HashSet<>();
				
				Set<Temp> outN_ = out.get(n);
				if (outN_ != null) {
					inN.addAll(outN_);
				}
				inN.removeAll(n.info.def());
				inN.addAll(n.info.use());
				
				if (!inN.equals(in.get(n.info))) {
					changed = true;
				}
				in.put(n.info, inN);
			}
		}
		while (changed);
		
		return new Pair<>(in, out);
	}
}
