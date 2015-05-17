package minij.parse.rules.statements;

import minij.parse.rules.expressions.Expression;
import minij.parse.rules.expressions.Id;

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
