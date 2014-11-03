package minijava.symboltable;

import java.util.HashMap;
import java.util.Map;

public class Program {
	private final Map<String, Class> classes;

	public Program() {
		classes = new HashMap<>();
	}

	public void add(Class clazz) {
		classes.put(clazz.name, clazz);
	}

	public Class get(String identifier) {
		return classes.get(identifier);
	}

	public boolean contains(String identifier) {
		return classes.containsKey(identifier);
	}
}
