package minijava.backend.i386;

import minijava.backend.Directive;
import minijava.backend.i386.visitors.AssemVisitor;
import minijava.translate.layout.Label;

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
		return this.accept(new I386PrintAssemblyVisitor());
	}

	@Override
	public <A, T extends Throwable> A accept(AssemVisitor<A, T> visitor) throws T {
		return visitor.visit(this);
	}
}
