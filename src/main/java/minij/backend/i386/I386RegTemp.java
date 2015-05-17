package minij.backend.i386;

import minij.translate.layout.Temp;

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
