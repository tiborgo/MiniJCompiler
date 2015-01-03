package minijava.ast.rules.expressions;

public interface ExpVisitor<A, T extends Throwable> {
	A visit(ExpTrue e) throws T;

	A visit(ExpFalse e) throws T;

	A visit(ExpThis e) throws T;

	A visit(ExpNewIntArray e) throws T;

	A visit(ExpNew e) throws T;

	A visit(ExpNeg e) throws T;

	A visit(ExpBinOp e) throws T;

	A visit(ExpArrayGet e) throws T;

	A visit(ExpArrayLength e) throws T;

	A visit(ExpInvoke e) throws T;

	A visit(ExpIntConst e) throws T;

	A visit(ExpId e) throws T;
}
