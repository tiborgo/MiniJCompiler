package minijava.ast.rules.declarations;

import java.util.List;

import minijava.ast.rules.Parameter;
import minijava.ast.rules.expressions.Exp;
import minijava.ast.rules.statements.Stm;
import minijava.ast.rules.types.Ty;

public class Method extends Declaration {

	final public Ty ty;
	final public String methodName;
	final public List<Parameter> parameters;
	final public List<Variable> localVars;
	final public Stm body;
	final public Exp returnExp;

	public Method(Ty ty, String methodName, List<Parameter> parameters,
	              List<Variable> localVars, Stm body, Exp returnExp) {
		this.ty = ty;
		this.methodName = methodName;
		this.parameters = parameters;
		this.localVars = localVars;
		this.body = body;
		this.returnExp = returnExp;
	}

	@Override
	public <A, T extends Throwable> A accept(DeclarationVisitor<A, T> v) throws T {
		return v.visit(this);
	}
}
