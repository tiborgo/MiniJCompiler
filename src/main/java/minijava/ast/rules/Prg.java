package minijava.ast.rules;

import java.util.List;

import minijava.ast.rules.declarations.Class;
import minijava.ast.rules.declarations.Main;

public class Prg {
	public final Main mainClass;
	public final List<Class> classes;

	public Prg(Main mainClass, List<Class> classes) {
		this.mainClass = mainClass;
		this.classes = classes;
	}

	public <A, T extends Throwable> A accept(PrgVisitor<A, T> v) throws T {
		return v.visit(this);
	}
}
