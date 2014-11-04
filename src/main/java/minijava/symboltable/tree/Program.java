package minijava.symboltable.tree;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Program implements Node {
	
	public final Map<String, Class> classes;

	public Program(List<Class> classes) {
		this.classes = new HashMap<>();
		for (Class clazz : classes) {
			this.classes.put(clazz.name, clazz);
		}
	}
	
	/*boolean contains(String className) {
		return classes.containsKey(className);
	}*/
	
	/*boolean contains(String className, String methodName, List<String> arguments) {
		
	}*/
	
	/*boolean contains(Class clazz, Method method, List<Variable> arguments) {
		
	}*/
}
