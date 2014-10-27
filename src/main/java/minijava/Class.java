package minijava;

import java.util.HashMap;
import java.util.Map;

public class Class {
	public final String name;
	public final Map<String, String> fields;
	public final Map<String, Method> methods;

	public Class(String name) {
		this.name = name;
		fields = new HashMap<>();
		methods = new HashMap<>();
	}
}
