package minijava.ast.rules;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import minijava.ast.rules.declarations.Class;
import minijava.ast.rules.declarations.Main;

public class Prg {
	public final Main mainClass;
	private final Map<String, Class> classes;

	public Prg(Main mainClass, List<Class> classes) {
		this.mainClass = mainClass;
		this.classes = new HashMap<>(classes.size());
		for (Class clazz : classes) {
			this.classes.put(clazz.className, clazz);
		}
	}

	public <A, T extends Throwable> A accept(PrgVisitor<A, T> v) throws T {
		return v.visit(this);
	}

	public Collection<Class> getClasses() {
		return Collections.unmodifiableCollection(classes.values());
	}
}
