package minijava.backend.i386;

import minijava.backend.Assem;

public abstract class I386Assem implements Assem {

	@Override
	public String toString() {
		return this.accept(new I386PrintAssemblyVisitor());
	}
}
