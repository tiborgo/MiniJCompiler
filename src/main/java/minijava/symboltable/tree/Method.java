package minijava.symboltable.tree;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import minijava.ast.rules.Ty;

public class Method implements Node {
	
	public final String name;
	public final Ty returnType;
	public final Map<String, Variable> parametersMap;
	public final Map<String, Variable> localVariables;
	public final List<Variable> parametersList;
	
	public Method(String name,
			Ty returnType,
			List<Variable> parameters,
			List<Variable> localVariables) {
		
		this.name = name;
		this.returnType = returnType;
		this.parametersMap = new HashMap<>();
		this.localVariables = new HashMap<>();
		this.parametersList = parameters;
		
		for (Variable parameter : parameters) {
			this.parametersMap.put(parameter.name, parameter);
		}
		
		for (Variable localVariable : localVariables) {
			this.localVariables.put(localVariable.name, localVariable);
		}
	}
}