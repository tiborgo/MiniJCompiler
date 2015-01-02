package minijava.backend.i386;

import minijava.backend.AssemVisitor;
import minijava.backend.Directive;
import minijava.intermediate.Label;

public final class AssemLabel extends Directive {
	public final Label label;

	public AssemLabel(Label label) {
		this.label = label;
	}

	@Override
	public Label isLabel() {
		return label;
	}

	@Override
	public String toString() {
		return label + ":\n";
	}

	@Override
	public <A, T extends Throwable> A accept(AssemVisitor<A, T> visitor) throws T {
		return visitor.visit(this);
	}
}
