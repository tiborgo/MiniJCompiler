package minij.backend.i386.assems;

import minij.backend.i386.visitors.I386PrintAssemblyVisitor;
import minij.instructionselection.assems.Directive;
import minij.translate.layout.Label;

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
