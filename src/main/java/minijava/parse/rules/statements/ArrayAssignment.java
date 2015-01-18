package minijava.parse.rules.statements;

import minijava.parse.rules.expressions.Expression;
import minijava.parse.rules.expressions.Id;

public class ArrayAssignment extends Statement {

	public final Id id;
	public final Expression index;
	public final Expression rhs;

	public ArrayAssignment(Id id, Expression index, Expression rhs) {
		this.id = id;
		this.index = index;
		this.rhs = rhs;
	}

	@Override
	public <A, T extends Throwable> A accept(StatementVisitor<A, T> v) throws T {
		return v.visit(this);
	}
}
