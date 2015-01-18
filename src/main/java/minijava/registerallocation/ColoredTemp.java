package minijava.registerallocation;

import minijava.translate.layout.Temp;

class ColoredTemp {
	public Temp color;
	public final Temp temp;
	
	ColoredTemp(Temp temp) {
		this(temp, null);
	}
	
	ColoredTemp(Temp temp, Temp color) {
		this.color = color;
		this.temp = temp;
	}
	
	boolean isColored() {
		return color != null;
	}
 	
	@Override
	public String toString() {
		return temp.toString() + " -> " + ((color != null) ? color.toString() : "<uncolored>");
	}
	
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof ColoredTemp && ((ColoredTemp)obj).temp.equals(temp)); 
	}
	
	@Override
	public int hashCode() {
		return temp.hashCode();
	}
}