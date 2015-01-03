package minijava.symboltable.tree;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Program implements Node {
	
	private final Map<String, Class> classes;

	public Program(List<Class> classes) {
		this.classes = new HashMap<>();
		for (Class clazz : classes) {
			this.classes.put(clazz.name, clazz);
		}
	}

	public boolean contains(String className) {
		return classes.containsKey(className);
	}

	public Class get(String className) {
		return classes.get(className);
	}
}
