package minijava.parse.rules;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import minijava.parse.rules.declarations.Class;

public class Program {
	private final Map<String, Class> classes;

	public Program(List<Class> classes) {

		this.classes = new HashMap<>(classes.size());
		for (Class clazz : classes) {
			this.classes.put(clazz.className, clazz);
		}
	}

	public <A, T extends Throwable> A accept(ProgramVisitor<A, T> v) throws T {
		return v.visit(this);
	}

	public Collection<Class> getClasses() {
		return Collections.unmodifiableCollection(classes.values());
	}

	public Class get(String className) {
		return classes.get(className);
	}

	public boolean contains(String className) {
		return classes.containsKey(className);
	}
}
