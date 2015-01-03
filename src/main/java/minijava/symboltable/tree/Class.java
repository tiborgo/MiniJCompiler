package minijava.symboltable.tree;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Class implements Node {
	public final String name;
	public final Map<String, Variable> fields;
	public final Map<String, Method> methods;
	
	public Class(String name,
			List<Variable> fields,
			List<Method> methods) {
		
		this.name = name;
		this.fields = new HashMap<>();
		this.methods = new HashMap<>();
		
		for (Variable field : fields) {
			this.fields.put(field.name, field);
		}
		
		for (Method method : methods) {
			this.methods.put(method.name, method);
		}
	}
}
