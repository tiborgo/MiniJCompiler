package minijava.symboltable;

public class Variable implements Entry {

	private final String name;
	private final String type;
	
	public Variable(String name, String type) {
		this.name = name;
		this.type = type;
	}
}
