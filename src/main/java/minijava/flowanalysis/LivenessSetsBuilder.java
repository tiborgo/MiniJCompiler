package minijava.flowanalysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import minijava.instructionselection.assems.Assem;
import minijava.translate.layout.Temp;
import minijava.util.SimpleGraph;

class LivenessSetsBuilder {

	static class InOut {
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

	static Map<Assem, InOut> build(SimpleGraph<Assem> controlFlowGraph, List<Assem> assems) {

		List<Assem> reverseAssems = new ArrayList<>(assems);
		Collections.reverse(reverseAssems);

		boolean changed;

		Map<Assem, InOut> inOut = new HashMap<>();

		for (Assem assem : reverseAssems) {
			inOut.put(assem, new InOut());
		}

		do {

			changed = false;

			for (Assem a : reverseAssems) {

				SimpleGraph.Node<Assem> n = controlFlowGraph.get(a);

				// old in and out set
				InOut inOutN_ = inOut.get(n.info);
				// new in and out set
				InOut inOutN = new InOut();

				// OUT
				for (SimpleGraph.Node<Assem> s : n.successors()) {
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
