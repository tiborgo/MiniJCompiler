package minijava.symboltable;

import java.util.HashMap;
import java.util.Map;

public class Class implements Entry {
	private final String name;
	private final Map<String, Variable> fields;
	private final Map<String, Method> methods;

	public Class(String name) {
		this.name = name;
		fields = new HashMap<>();
		methods = new HashMap<>();
	}
	
	public void add(Variable field) {
		fields.put(field.getName(), field);
	}
	
	public void add(Method method) {
		methods.put(method.getName(), method);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean contains(Entry entry) {
		return false;
	}
	
	public boolean contains(Variable field) {
		return fields.containsKey(field.getName());
	}
	
	public boolean contains(Method method) {
		return methods.containsKey(method.getName());
	}
}
