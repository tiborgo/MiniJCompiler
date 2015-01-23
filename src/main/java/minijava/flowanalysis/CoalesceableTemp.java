package minijava.flowanalysis;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import minijava.instructionselection.assems.Assem;
import minijava.translate.layout.Temp;
import minijava.util.Pair;

public class CoalesceableTemp {
	public Temp color;
	public final Temp temp;
	private Map<Temp, Set<Assem>> moves = new HashMap<>();
	//private Set<Temp> partners = new HashSet<>();
	
	public CoalesceableTemp(Temp temp) {
		this(temp, null);
	}
	
	public CoalesceableTemp(Temp temp, Temp color) {
		this.color = color;
		this.temp = temp;
	}
	
	public boolean isColored() {
		return color != null;
	}
	
	/*public boolean isMoveRelated() {
		return (moves.size() > 0);
	}*/
	
	public void addMove(Assem move) {
		Pair<Temp, Temp> moveTemps = move.isMoveBetweenTemps();
		
		assert(moveTemps != null);
		assert(moveTemps.fst.equals(temp) || moveTemps.snd.equals(temp));
		
		Temp partner = (moveTemps.fst.equals(temp)) ? moveTemps.snd : moveTemps.fst;
		//partners.add(partner);
		if (moves.get(partner) == null) {
			moves.put(partner, new HashSet<Assem>());
		}
		moves.get(partner).add(move);
	}
	
	/*public void removeMove(Assem move) {
		int i = moves.indexOf(move);
		assert(i != -1);
		moves.remove(i);
		partners.remove(i);
	}*/
	
	public void removePartner(Temp partner) {
		//partners.remove(partner);
		moves.remove(partner);
		/*for (Assem move : moves) {
			Pair<Temp, Temp> temps = move.isMoveBetweenTemps();
			Temp t = (temps.fst.equals(temp)) ? temps.snd : temps.fst;
			moves.remove(move);
		}*/
	}
	
	public Set<Pair<Temp, Temp>> getMoveTemps(Temp partner) {
		
		Set<Assem> moves_ = moves.get(partner);
		assert(moves_ != null);
		Set<Pair<Temp, Temp>> temps = new HashSet<>();
		for (Assem move : moves_) {
			temps.add(move.isMoveBetweenTemps());
		}
		return temps;
	}
 	
	@Override
	public String toString() {
		return temp.toString() + " -> " + ((color != null) ? color.toString() : "<uncolored>");
	}
	
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof CoalesceableTemp && ((CoalesceableTemp)obj).temp.equals(temp)); 
	}
	
	@Override
	public int hashCode() {
		return temp.hashCode();
	}

	public Set<Temp> getPartners() {
		return moves.keySet();
	}

	public void free() {
		//partners.clear();
		moves.clear();
	}
}