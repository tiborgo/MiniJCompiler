package minijava;

import java.util.HashMap;
import java.util.Map;

public class Method implements SymbolTableEntry {
	public final String name;
	public final Map<String, String> parameters;
	public final Map<String, String> localVariables;

	public Method(String name) {
		this.name = name;
		parameters = new HashMap<>();
		localVariables = new HashMap<>();
	}

	@Override
	public boolean contains(SymbolTableEntry entry) {
		/*if (entry instanceof Variable) {
			Variable
		}*/
		return false;
	}
}
