package minijava.ast.visitors;

import java.util.LinkedList;

import minijava.ast.rules.DeclClass;
import minijava.ast.rules.DeclMain;
import minijava.ast.rules.DeclMeth;
import minijava.ast.rules.DeclVar;
import minijava.ast.rules.Parameter;
import minijava.ast.rules.Prg;
import minijava.symboltable.Class;
import minijava.symboltable.Entry;
import minijava.symboltable.Method;
import minijava.symboltable.Program;
import minijava.symboltable.Variable;

public class SymbolTableVisitor implements
		PrgVisitor<Program, RuntimeException>,
		DeclVisitor<Entry, RuntimeException>,
		ParameterVisitor<Variable, RuntimeException> {
	
	private LinkedList<String> types = new LinkedList<>();
	
	public SymbolTableVisitor() {
		types.add("int");
		types.add("int[]");
		types.add("boolean");
	}

	@Override
	public Program visit(Prg p) throws RuntimeException {
		
		LinkedList<Class> classes = new LinkedList<>();
		
		Class mainClass = visit(p.mainClass);
		classes.add(mainClass);
		types.add(mainClass.name);
		
		for (DeclClass clazzDecl : p.classes) {
			Class clazz = visit(clazzDecl);
			classes.add(clazz);
			types.add(clazz.name);
		}

		return new Program(classes);
	}

	@Override
	public Class visit(DeclClass c) throws RuntimeException {

		LinkedList<Variable> fields = new LinkedList<>();
		for (DeclVar field : c.fields) {
			fields.add((Variable) visit(field));
		}

		LinkedList<Method> methods = new LinkedList<>();
		for (DeclMeth field : c.methods) {
			methods.add(visit(field));
		}

		if (types.contains(c.className)) {
			System.err.println("Type '" + c.className + "' does already exist!");
		}
		
		return new Class(c.className, fields, methods);
	}

	@Override
	public Class visit(DeclMain d) throws RuntimeException {
		return new Class(d.className);
	}

	@Override
	public Method visit(DeclMeth m) throws RuntimeException {

		LinkedList<Variable> parameters = new LinkedList<>();
		for (Parameter parameter : m.parameters) {
			parameters.add(visit(parameter));
		}

		LinkedList<Variable> localVariables = new LinkedList<>();
		for (DeclVar localVariable : m.localVars) {
			localVariables.add(visit(localVariable));
		}

		return new Method(m.methodName, m.ty.toString(), parameters, localVariables);
	}

	@Override
	public Variable visit(DeclVar d) throws RuntimeException {
		return new Variable(d.name, d.ty.toString());
	}

	@Override
	public Variable visit(Parameter p) throws RuntimeException {
		return new Variable(p.id, p.ty.toString());
	}
}