/*
 * MiniJCompiler: Compiler for a subset of the Java(R) language
 *
 * (C) Copyright 2014-2015 Tibor Goldschwendt <goldschwendt@cip.ifi.lmu.de>,
 * Michael Seifert <mseifert@error-reports.org>
 *
 * This file is part of MiniJCompiler.
 *
 * MiniJCompiler is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MiniJCompiler is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MiniJCompiler.  If not, see <http://www.gnu.org/licenses/>.
 */
package minij.flowanalysis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import minij.instructionselection.assems.Assem;
import minij.translate.layout.Temp;
import minij.util.Pair;

public class CoalesceableTemp {
	public Temp color;
	public final Temp temp;
	private Map<Temp, Set<Assem>> moves = new HashMap<>();
	
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
	
	public void addMove(Assem move) {
		Pair<Temp, Temp> moveTemps = move.isMoveBetweenTemps();
		
		assert(moveTemps != null);
		assert(moveTemps.fst.equals(temp) || moveTemps.snd.equals(temp));
		
		Temp partner = (moveTemps.fst.equals(temp)) ? moveTemps.snd : moveTemps.fst;
		if (moves.get(partner) == null) {
			moves.put(partner, new HashSet<Assem>());
		}
		moves.get(partner).add(move);
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
}