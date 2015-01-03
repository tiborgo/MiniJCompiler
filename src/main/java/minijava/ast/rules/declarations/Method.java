package minijava.ast.rules.declarations;

import java.util.List;

import minijava.ast.rules.Parameter;
import minijava.ast.rules.expressions.Expression;
import minijava.ast.rules.statements.Statement;
import minijava.ast.rules.types.Ty;

public class Method extends Declaration {

	final public Ty ty;
	final public String methodName;
	final public List<Parameter> parameters;
	final public List<Variable> localVars;
	final public Statement body;
	final public Expression returnExpression;

	public Method(Ty ty, String methodName, List<Parameter> parameters,
	              List<Variable> localVars, Statement body, Expression returnExpression) {
		this.ty = ty;
		this.methodName = methodName;
		this.parameters = parameters;
		this.localVars = localVars;
		this.body = body;
		this.returnExpression = returnExpression;
	}

	@Override
	public <A, T extends Throwable> A accept(DeclarationVisitor<A, T> v) throws T {
		return v.visit(this);
	}
}
