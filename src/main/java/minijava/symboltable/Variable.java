package minijava.symboltable;

public class Variable implements Entry {

	public final String name;
	public final String type;
	
	public Variable(String name, String type) {
		this.name = name;
		this.type = type;
	}
}
