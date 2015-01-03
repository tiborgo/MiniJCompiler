package minijava.ast.rules.declarations;

import java.util.List;

import minijava.ast.rules.Parameter;
import minijava.ast.rules.expressions.Expression;
import minijava.ast.rules.statements.Statement;
import minijava.ast.rules.types.Type;

public class Method extends Declaration {

	final public Type type;
	final public String methodName;
	final public List<Parameter> parameters;
	final public List<Variable> localVars;
	final public Statement body;
	final public Expression returnExpression;


	public Method(Type type, String methodName, List<Parameter> parameters,
	              List<Variable> localVars, Statement body, Expression returnExpression) {
		this.type = type;
		this.methodName = methodName;
		this.parameters = parameters;
		this.localVars = localVars;
		this.body = body;
		this.returnExpression = returnExpression;
	}

	public Variable get(String variableName) {
		for (Parameter parameter : parameters) {
			if (parameter.id.equals(variableName)) {
				// FIXME: Do not create a new instance
				return new Variable(parameter.type, parameter.id);
			}
		}

		for (Variable variable : localVars) {
			if (variable.name.equals(variableName)) {
				return variable;
			}
		}
		return null;
	}

	@Override
	public <A, T extends Throwable> A accept(DeclarationVisitor<A, T> v) throws T {
		return v.visit(this);
	}
}
