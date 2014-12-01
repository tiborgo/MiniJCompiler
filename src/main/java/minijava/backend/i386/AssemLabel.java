package minijava.backend.i386;

import java.util.Collections;
import java.util.List;

import minijava.backend.Assem;
import minijava.backend.AssemVisitor;
import minijava.intermediate.Label;
import minijava.intermediate.Temp;
import minijava.util.Function;
import minijava.util.Pair;

public final class AssemLabel implements Assem {

	private final Label label;

	public AssemLabel(Label label) {
		this.label = label;
	}

	public List<Temp> use() {
		return Collections.emptyList();
	}

	public List<Temp> def() {
		return Collections.emptyList();
	}

	public List<Label> jumps() {
		return Collections.emptyList();
	}

	public boolean isFallThrough() {
		return true;
	}

	public Pair<Temp, Temp> isMoveBetweenTemps() {
		return null;
	}

	public Label isLabel() {
		return label;
	}

	public String toString() {
		return label + ":\n";
	}

	public Assem rename(Function<Temp, Temp> sigma) {
		return this;
	}

	@Override
	public <A, T extends Throwable> A accept(AssemVisitor<A, T> visitor)
			throws T {
		return visitor.visit(this);
	}
}
