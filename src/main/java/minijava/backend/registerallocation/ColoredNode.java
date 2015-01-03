package minijava.backend.registerallocation;

import minijava.intermediate.Temp;

public class ColoredNode {
	public Temp color;
	public final Temp temp;
	
	public ColoredNode(Temp temp, Temp color) {
		this.color = color;
		this.temp = temp;
	}
	
	public String toString() {
		return temp.toString() + " -> " + ((color != null) ? color.toString() : "<uncolored>");
	}
}