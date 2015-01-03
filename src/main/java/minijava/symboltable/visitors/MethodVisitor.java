package minijava.symboltable.visitors;

import minijava.ast.rules.declarations.*;
import minijava.ast.rules.declarations.Class;
import minijava.symboltable.tree.Program;


public class MethodVisitor implements SymbolTableVisitor<Method, RuntimeException> {

	private final Class clazz;
	private final String methodName;
	
	public MethodVisitor(minijava.ast.rules.declarations.Class clazz, String methodName) {
		this.clazz = clazz;
		this.methodName = methodName;
	}

	@Override
	public Method visit(Program program) throws RuntimeException {
		return null;
	}
}
