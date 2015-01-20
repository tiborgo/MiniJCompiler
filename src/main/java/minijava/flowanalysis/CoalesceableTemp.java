package minijava.flowanalysis;

import minijava.translate.layout.Temp;

public class CoalesceableTemp {
	public Temp color;
	public final Temp temp;
	public boolean moveRelated;
	
	public CoalesceableTemp(Temp temp) {
		this(temp, null, false);
	}
	
	public CoalesceableTemp(Temp temp, Temp color, boolean moveRelated) {
		this.color = color;
		this.temp = temp;
		this.moveRelated = moveRelated;
	}
	
	public boolean isColored() {
		return color != null;
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
}