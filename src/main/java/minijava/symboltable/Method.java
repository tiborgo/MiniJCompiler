package minijava.symboltable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Method implements Entry {
	
	public final String name;
	public final String returnType;
	public final Map<String, Variable> parameters;
	public final Map<String, Variable> localVariables;

	public Method(String name,
			String returnType,
			List<Variable> parameters,
			List<Variable> localVariables) {
		
		this.name = name;
		this.returnType = returnType;
		this.parameters = new HashMap<>();
		this.localVariables = new HashMap<>();
		
		for (Variable parameter : parameters) {
			this.parameters.put(parameter.name, parameter);
		}
		
		for (Variable localVariable : localVariables) {
			this.localVariables.put(localVariable.name, localVariable);
		}
	}
}
