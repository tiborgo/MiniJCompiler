package minijava.symboltable.visitors;

import minijava.ast.rules.declarations.Method;
import minijava.symboltable.tree.Class;
import minijava.symboltable.tree.Program;


public class MethodVisitor implements SymbolTableVisitor<Method, RuntimeException> {

	private final Class clazz;
	private final String methodName;
	
	public MethodVisitor(Class clazz, String methodName) {
		this.clazz = clazz;
		this.methodName = methodName;
	}

	@Override
	public Method visit(Program program) throws RuntimeException {
		return visit(clazz);
	}

	@Override
	public Method visit(Class clazz) throws RuntimeException {
		Method method = clazz.methods.get(methodName);
		
		if (method == null) {
			// TODO: error string
			return null;
		}
		else {
			return method;
		}
	}
}
