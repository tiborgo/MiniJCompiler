package minijava.ast.rules.declarations;

import java.util.List;

import minijava.ast.rules.Parameter;
import minijava.ast.rules.expressions.Exp;
import minijava.ast.rules.statements.Stm;
import minijava.ast.rules.types.Ty;

public class DeclMeth extends Decl {

	final public Ty ty;
	final public String methodName;
	final public List<Parameter> parameters;
	final public List<DeclVar> localVars;
	final public Stm body;
	final public Exp returnExp;

	public DeclMeth(Ty ty, String methodName, List<Parameter> parameters,
			List<DeclVar> localVars, Stm body, Exp returnExp) {
		this.ty = ty;
		this.methodName = methodName;
		this.parameters = parameters;
		this.localVars = localVars;
		this.body = body;
		this.returnExp = returnExp;
	}

	@Override
	public <A, T extends Throwable> A accept(DeclVisitor<A, T> v) throws T {
		return v.visit(this);
	}
}
