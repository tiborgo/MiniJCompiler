package minijava.backend.livenessanalysis;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import minijava.backend.Assem;
import minijava.intermediate.Temp;
import minijava.util.Pair;
import minijava.util.SimpleGraph;

public class LivenessSetsBuilder {
	
	public static Pair<Map<Assem, Set<Temp>>, Map<Assem, Set<Temp>>> build(SimpleGraph<Assem> controlFlowGraph) {
		
		final List<SimpleGraph<Assem>.Node> nodes = ReverseOrderBuilder.build(controlFlowGraph);
		
		boolean changed;
		
		Comparator<Assem> comparator = new Comparator<Assem>() {

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
				return iO1 - iO2;
			}
		};
		
		Map<Assem, Set<Temp>> in = new TreeMap<>(comparator);
		Map<Assem, Set<Temp>> out = new TreeMap<>(comparator);
		
		for (int i = 0; i < nodes.size(); i++) {
			in.put(nodes.get(i).info, new HashSet<Temp>());
			out.put(nodes.get(i).info, new HashSet<Temp>());
		}
		
		do {
		
			changed = false;
			
			//for (int i = 0; i < nodes.size(); i++) {
			for (SimpleGraph<Assem>.Node n : nodes) {
				
				//SimpleGraph<Assem>.Node n = nodes.get(i);
				
				// OUT
				Set<Temp> outN = new HashSet<>();
				
				for (SimpleGraph<Assem>.Node s : n.successors()) {
					Set<Temp> inS = in.get(s.info);
					outN.addAll(inS);
				}
				
				if (!outN.equals(out.get(n.info))) {
					changed = true;
					out.put(n.info, outN);
				}
				
				// IN
				Set<Temp> inN = new HashSet<>();
				
				Set<Temp> outN_ = out.get(n.info);
				inN.addAll(outN_);
				List<Temp> def = n.info.def();
				inN.removeAll(def);
				List<Temp> use = n.info.use();
				inN.addAll(use);
				
				if (!inN.equals(in.get(n.info))) {
					changed = true;
					in.put(n.info, inN);
				}
			}
		}
		while (changed);
		
		return new Pair<>(in, out);
	}
}
