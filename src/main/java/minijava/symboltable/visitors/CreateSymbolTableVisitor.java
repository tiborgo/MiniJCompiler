package minijava.symboltable.visitors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import minijava.ast.rules.Parameter;
import minijava.ast.rules.ParameterVisitor;
import minijava.ast.rules.Prg;
import minijava.ast.rules.PrgVisitor;
import minijava.ast.rules.declarations.Class;
import minijava.ast.rules.declarations.DeclarationVisitor;
import minijava.ast.rules.declarations.Main;
import minijava.ast.rules.declarations.Method;
import minijava.ast.rules.declarations.Variable;
import minijava.ast.rules.types.Array;
import minijava.ast.rules.types.Integer;
import minijava.symboltable.tree.Node;
import minijava.symboltable.tree.Program;

public class CreateSymbolTableVisitor implements
		PrgVisitor<Program, RuntimeException>,
		DeclarationVisitor<Node, RuntimeException>,
		ParameterVisitor<Variable, RuntimeException> {
	
	private LinkedList<String> types = new LinkedList<>();
	
	public CreateSymbolTableVisitor() {
		types.add("int");
		types.add("int[]");
		types.add("boolean");
	}

	@Override
	public Program visit(Prg p) throws RuntimeException {
		
		LinkedList<minijava.symboltable.tree.Class> classes = new LinkedList<>();
		
		minijava.symboltable.tree.Class mainClass = visit(p.mainClass);
		classes.add(mainClass);
		types.add(mainClass.name);
		
		for (Class clazzDecl : p.getClasses()) {
			minijava.symboltable.tree.Class clazz = visit(clazzDecl);
			classes.add(clazz);
			types.add(clazz.name);
		}

		return new Program(classes);
	}

	@Override
	public minijava.symboltable.tree.Class visit(Class c) throws RuntimeException {

		List<Variable> fields = new ArrayList<>(c.fields);

		LinkedList<minijava.symboltable.tree.Method> methods = new LinkedList<>();
		for (Method field : c.methods) {
			methods.add(visit(field));
		}

		if (types.contains(c.className)) {
			System.err.println("Type '" + c.className + "' does already exist!");
		}
		
		return new minijava.symboltable.tree.Class(c.className, fields, methods);
	}

	@Override
	public minijava.symboltable.tree.Class visit(Main d) throws RuntimeException {
		return new minijava.symboltable.tree.Class(
			"",
			Collections.<Variable>emptyList(),
			Arrays.asList(new minijava.symboltable.tree.Method(
				"main",
				new Integer(),
				Arrays.asList(new Variable(new Array(new Integer()), d.mainArg)),
				Collections.<Variable>emptyList()
			))
		);
	}

	@Override
	public minijava.symboltable.tree.Method visit(Method m) throws RuntimeException {

		LinkedList<Variable> parameters = new LinkedList<>();
		for (Parameter parameter : m.parameters) {
			parameters.add(visit(parameter));
		}

		List<Variable> localVariables = new ArrayList<>(m.localVars);

		return new minijava.symboltable.tree.Method(m.methodName, m.type, parameters, localVariables);
	}

	@Override
	public Node visit(Variable d) throws RuntimeException {
		// TODO: obsolete
		return null;
	}

	@Override
	public Variable visit(Parameter p) throws RuntimeException {
		return new Variable(p.type, p.id);
	}
}