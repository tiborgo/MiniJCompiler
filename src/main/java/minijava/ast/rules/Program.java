package minijava.ast.rules;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import minijava.ast.rules.declarations.Class;
import minijava.ast.rules.declarations.Main;

public class Program {
	public final Main mainClass;
	private final Map<String, Class> classes;

	public Program(Main mainClass, List<Class> classes) {
		this.mainClass = mainClass;

		this.classes = new HashMap<>(classes.size());
		this.classes.put(mainClass.className, mainClass);
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
