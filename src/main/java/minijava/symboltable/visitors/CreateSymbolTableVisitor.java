package minijava.symboltable.visitors;

import java.util.LinkedList;

import minijava.ast.rules.Parameter;
import minijava.ast.rules.ParameterVisitor;
import minijava.ast.rules.Prg;
import minijava.ast.rules.PrgVisitor;
import minijava.ast.rules.declarations.Class;
import minijava.ast.rules.declarations.DeclarationVisitor;
import minijava.ast.rules.declarations.Main;
import minijava.ast.rules.declarations.Method;
import minijava.ast.rules.declarations.Variable;
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
		
		LinkedList<Class> classes = new LinkedList<>(p.getClasses());

		// TODO: Add main class

		for (Class clazz : p.getClasses()) {
			types.add(clazz.className);
		}

		return new Program(classes);
	}

	@Override
	public Node visit(Class c) throws RuntimeException {
		// TODO: obsolete
		return null;
	}

	@Override
	public Node visit(Main d) throws RuntimeException {
		// TODO: obsolete
		return null;
	}

	@Override
	public Node visit(Method m) throws RuntimeException {
		// TODO: obsolete
		return null;
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