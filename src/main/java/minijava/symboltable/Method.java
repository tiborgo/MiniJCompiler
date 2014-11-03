package minijava.symboltable;

import java.util.HashMap;
import java.util.Map;

public class Method implements Entry {
	private final String name;
	private final String returnType;
	private final Map<String, Variable> parameters;
	private final Map<String, Variable> localVariables;

	public Method(String name, String returnType) {
		this.name = name;
		this.returnType = returnType;
		parameters = new HashMap<>();
		localVariables = new HashMap<>();
	}

	public void addParameters(Variable... parameters) {
		for (Variable variable : parameters) {
			this.parameters.put(variable.getName(), variable);
		}
	}
	
	public void addLocalVariable(Variable... localVariables) {
		for (Variable variable : localVariables) {
			this.localVariables.put(variable.getName(), variable);
		}
	}
	
	@Override
	public boolean contains(Entry entry) {
		/*if (entry instanceof Variable) {
			Variable
		}*/
		return false;
	}
}
