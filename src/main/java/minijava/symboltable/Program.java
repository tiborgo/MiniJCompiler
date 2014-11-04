package minijava.symboltable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Program {
	
	public final Map<String, Class> classes;

	public Program(List<Class> classes) {
		this.classes = new HashMap<>();
		for (Class clazz : classes) {
			this.classes.put(clazz.name, clazz);
		}
	}
}
