package minijava.ast.rules.statements;

import minijava.ast.rules.expressions.Expression;
import minijava.ast.rules.expressions.Id;

public class Assignment extends Statement {

	public final Id id;
	public final Expression rhs;

	public Assignment(Id id, Expression rhs) {
		this.id = id;
		this.rhs = rhs;
	}

	@Override
	public <A, T extends Throwable> A accept(StatementVisitor<A, T> v) throws T {
		return v.visit(this);
	}
}
