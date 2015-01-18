package minijava.parse.rules.expressions;

public interface ExpressionVisitor<A, T extends Throwable> {
	A visit(True e) throws T;

	A visit(False e) throws T;

	A visit(This e) throws T;

	A visit(NewIntArray e) throws T;

	A visit(New e) throws T;

	A visit(Negate e) throws T;

	A visit(BinOp e) throws T;

	A visit(ArrayGet e) throws T;

	A visit(ArrayLength e) throws T;

	A visit(Invoke e) throws T;

	A visit(IntConstant e) throws T;

	A visit(Id e) throws T;
}
