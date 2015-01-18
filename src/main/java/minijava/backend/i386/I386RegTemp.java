package minijava.backend.i386;

import minijava.translate.Temp;

public class I386RegTemp extends Temp {
	public final String name;

	public I386RegTemp(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
}
