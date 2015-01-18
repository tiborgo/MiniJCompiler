package minijava.backend;

import java.util.Collections;
import java.util.List;

import minijava.backend.i386.visitors.AssemVisitor;
import minijava.translate.Label;
import minijava.translate.Temp;
import minijava.util.Function;
import minijava.util.Pair;

public abstract class Directive implements Assem {

	@Override
	public List<Temp> use() {
		return Collections.emptyList();
	}

	@Override
	public List<Temp> def() {
		return Collections.emptyList();
	}

	@Override
	public List<Label> jumps() {
		return Collections.emptyList();
	}

	@Override
	public boolean isFallThrough() {
		return true;
	}

	@Override
	public Pair<Temp, Temp> isMoveBetweenTemps() {
		return null;
	}

	@Override
	public Assem rename(Function<Temp, Temp> sigma) {
		return this;
	}

	@Override
	public <A, T extends Throwable> A accept(AssemVisitor<A, T> visitor) throws T {
		return visitor.visit(this);
	}
}
