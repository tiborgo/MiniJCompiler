package minijava.registerallocation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import minijava.flowanalysis.CoalesceableTemp;
import minijava.instructionselection.assems.Assem;
import minijava.translate.layout.Temp;
import minijava.util.Function;
import minijava.util.Pair;
import minijava.util.SimpleGraph;
import minijava.util.SimpleGraph.Node;

public class Coalescer {

	static boolean coalesce(SimpleGraph<CoalesceableTemp> graph, List<Assem> allocatedBody, int k, String methodName) {
		
		final Map<Temp, Temp> renames = new HashMap<>();
		boolean changed = false;
		boolean loopChanged;
		
		// Slide 295
		//if (a.info.isMoveRelated()) {
		do {
			
			loopChanged = false;
			
			nodeLoop:
			for (Node<CoalesceableTemp> a : graph.nodeSet()) {
			
			
				for (Node<CoalesceableTemp> b : a.secondarySuccessors()) {
					//Node<CoalesceableTemp> b = graph.get(new CoalesceableTemp(tB));
					/*Set<Pair<Temp, Temp>> moveTempsSet = a.info.getMoveTemps(b.info.temp);
					Temp removeColor = null;
					Temp coalascedColor = null;
					Pair<Temp, Temp> moveTemps = null;
					for (Pair<Temp, Temp> temps : moveTempsSet) {
						moveTemps = temps;
						removeColor = (b.info.temp.equals(temps.fst)) ? ((b != null) ? b.info.color : null) : a.info.color;
						coalascedColor = (b.info.temp.equals(temps.snd) && b != null) ? b.info.color : a.info.color;
						if (removeColor == null) {
							break;
						}
					}*/
					
					// Cannot remove colored temp since registers can have a special purpose
					if (/*!a.neighbours().contains(b) &&*/ b.info.color == null) {
					
						boolean coalesceable = false;
						
						Set<Node<CoalesceableTemp>> neighbours = new HashSet<>(a.neighbours());
						
						// tB is not used at all
						if (b == null) {
							coalesceable = true;
						}
						else {
							neighbours.addAll(b.neighbours());
						}
						
						neighbours.remove(a);
						neighbours.remove(b);
						
						if (!coalesceable) {
						
							// BRIGGS
							
							int kNeighboursCount = 0;
							for (Node<CoalesceableTemp> n : neighbours) {
								if (n.degree() >= k) {
									kNeighboursCount++;
								}
							}
							if (kNeighboursCount < k) {
								coalesceable = true;
							}
						}
						
						// GEORGE
						if (!coalesceable) {
							// TODO: implement
						}
						
						// coalesce
						if (coalesceable) {
							changed = true;
							loopChanged = true;
							/*graph.removeNode(a);
							if (b != null) {
								graph.removeNode(b);
							}
		
							Node<CoalesceableTemp> coalescedNode = graph.addNode(new CoalesceableTemp(moveTemps.snd, coalascedColor));
							for (Node<CoalesceableTemp> neighbour : neighbours) {
								graph.addEdge(coalescedNode, neighbour);
							}*/
							
							graph.merge(a, b, a.info);
							
							renames.put(b.info.temp, a.info.temp);
							
							break nodeLoop;
						}
					}
				}
			}
		}
		while(loopChanged);
		
		
		for (int i = 0; i < allocatedBody.size(); i++) {
			
			Assem coalescedAssem = allocatedBody.get(i).rename(new Function<Temp, Temp>() {

				@Override
				public Temp apply(Temp t) {
					Temp newT = renames.get(t);
					return (newT != null) ? newT : t;
				}
			});
			
			Pair<Temp, Temp> move = coalescedAssem.isMoveBetweenTemps();
			if (move != null && move.fst.equals(move.snd)) {
				allocatedBody.remove(i);
				i--;
			}
			else {
				allocatedBody.set(i, coalescedAssem);
			}
		}
		
		//System.out.println(methodName);
		//System.out.println(renames);
		
		return changed;
	}
}
