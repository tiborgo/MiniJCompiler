package minijava.ast.rules.statements;

import minijava.ast.rules.expressions.Expression;

public class Assignment extends Statement {

	public final String id;
	public final Expression rhs;

	public Assignment(String id, Expression rhs) {
		this.id = id;
		this.rhs = rhs;
	}

	@Override
	public <A, T extends Throwable> A accept(StatementVisitor<A, T> v) throws T {
		return v.visit(this);
	}
}
