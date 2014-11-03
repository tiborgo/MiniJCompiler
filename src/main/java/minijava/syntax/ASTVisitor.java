package minijava.syntax;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.antlr.v4.runtime.misc.NotNull;

import minijava.MiniJavaBaseVisitor;
import minijava.MiniJavaParser;
import minijava.MiniJavaParser.ArrayAccessExpressionContext;
import minijava.MiniJavaParser.ArrayLengthExpressionContext;
import minijava.MiniJavaParser.BinOpExpressionContext;
import minijava.MiniJavaParser.ExpressionContext;
import minijava.MiniJavaParser.FalseExpressionContext;
import minijava.MiniJavaParser.IdentifierExpressionContext;
import minijava.MiniJavaParser.IntegerLiteralExpressionContext;
import minijava.MiniJavaParser.InvokeExpressionContext;
import minijava.MiniJavaParser.NewExpressionContext;
import minijava.MiniJavaParser.NewIntArrayExpressionContext;
import minijava.MiniJavaParser.NotExpressionContext;
import minijava.MiniJavaParser.ThisExpressionContext;
import minijava.MiniJavaParser.TrueExpressionContext;
import minijava.syntax.ast.Exp;
import minijava.syntax.ast.ExpArrayGet;
import minijava.syntax.ast.ExpArrayLength;
import minijava.syntax.ast.ExpBinOp;
import minijava.syntax.ast.ExpBinOp.Op;
import minijava.syntax.ast.ExpFalse;
import minijava.syntax.ast.ExpId;
import minijava.syntax.ast.ExpIntConst;
import minijava.syntax.ast.ExpInvoke;
import minijava.syntax.ast.ExpNeg;
import minijava.syntax.ast.ExpNew;
import minijava.syntax.ast.ExpThis;
import minijava.syntax.ast.ExpTrue;

public class ASTVisitor extends MiniJavaBaseVisitor<Object> {

	@Override public Object visitIdentifier(@NotNull MiniJavaParser.IdentifierContext ctx) { return visitChildren(ctx); }

	@Override public Object visitMethodDeclaration(@NotNull MiniJavaParser.MethodDeclarationContext ctx) { return visitChildren(ctx); }

	@Override public Object visitBooleanType(@NotNull MiniJavaParser.BooleanTypeContext ctx) { return visitChildren(ctx); }

	@Override public Object visitMainClass(@NotNull MiniJavaParser.MainClassContext ctx) { return visitChildren(ctx); }

	@Override public Object visitArrayAssignStatement(@NotNull MiniJavaParser.ArrayAssignStatementContext ctx) { return visitChildren(ctx); }

	@Override public Object visitIntType(@NotNull MiniJavaParser.IntTypeContext ctx) { return visitChildren(ctx); }

	@Override public Object visitIntArrayType(@NotNull MiniJavaParser.IntArrayTypeContext ctx) { return visitChildren(ctx); }

	@Override public Object visitBracketStatement(@NotNull MiniJavaParser.BracketStatementContext ctx) { return visitChildren(ctx); }

	@Override public Object visitIfStatement(@NotNull MiniJavaParser.IfStatementContext ctx) { return visitChildren(ctx); }

	@Override public Object visitProg(@NotNull MiniJavaParser.ProgContext ctx) { return visitChildren(ctx); }

	@Override public Object visitVarDeclaration(@NotNull MiniJavaParser.VarDeclarationContext ctx) { return visitChildren(ctx); }

	@Override public Object visitOtherType(@NotNull MiniJavaParser.OtherTypeContext ctx) { return visitChildren(ctx); }

	@Override public Object visitClassDeclaration(@NotNull MiniJavaParser.ClassDeclarationContext ctx) { return visitChildren(ctx); }

	@Override public Object visitWhileStatement(@NotNull MiniJavaParser.WhileStatementContext ctx) { return visitChildren(ctx); }

	@Override public Object visitSystemOutPrintlnStatement(@NotNull MiniJavaParser.SystemOutPrintlnStatementContext ctx) { return visitChildren(ctx); }

	@Override public Object visitAssignStatement(@NotNull MiniJavaParser.AssignStatementContext ctx) { return visitChildren(ctx); }

	@Override public Object visitSystemOutPrintStatement(@NotNull MiniJavaParser.SystemOutPrintStatementContext ctx) { return visitChildren(ctx); }

	@Override
	public Object visitThisExpression(ThisExpressionContext ctx) {
		return new ExpThis();
	}

	@Override
	public Object visitBinOpExpression(BinOpExpressionContext ctx) {
		ExpBinOp.Op binOp;
		binOp = ctx.MINUS() != null ? Op.MINUS : null;
		binOp = ctx.PLUS() != null ? Op.PLUS : null;
		binOp = ctx.SLASH() != null ? Op.DIV : null;
		binOp = ctx.STAR() != null ? Op.TIMES : null;
		binOp = ctx.SMALLER() != null ? Op.LT : null;
		binOp = ctx.DOUBLE_AMPERSAND() != null ? Op.AND : null;
		Exp expression0 = (Exp) visit(ctx.expression(0));
		Exp expression1 = (Exp) visit(ctx.expression(1));
		return new ExpBinOp(expression0, binOp, expression1);
	}

	@Override
	public Object visitIntegerLiteralExpression(IntegerLiteralExpressionContext ctx) {
		Integer value = Integer.parseInt(ctx.INTEGER_LITERAL().getText());
		return new ExpIntConst(value);
	}

	@Override
	public Object visitArrayAccessExpression(ArrayAccessExpressionContext ctx) {
		Exp arrayExpression = (Exp) visit(ctx.expression(0));
		Exp indexExpression = (Exp) visit(ctx.expression(1));
		return new ExpArrayGet(arrayExpression, indexExpression);
	}

	@Override
	public Object visitTrueExpression(TrueExpressionContext ctx) {
		return new ExpTrue();
	}

	@Override
	public Object visitInvokeExpression(InvokeExpressionContext ctx) {
		Exp object = (Exp) visit(ctx.expression(0));
		String method = ctx.identifier().getText();
		List<ExpressionContext> argumentsRaw = ctx.expression().subList(1, ctx.expression().size() - 1);
		List<Exp> arguments = new ArrayList<>(argumentsRaw.size() - 1);
		for (ExpressionContext argumentRaw : argumentsRaw) {
			Exp argument = (Exp) visit(argumentRaw);
			arguments.add(argument);
		}
		return new ExpInvoke(object, method, arguments);
	}

	@Override
	public Object visitIdentifierExpression(IdentifierExpressionContext ctx) {
		String id = ctx.identifier().getText();
		return new ExpId(id);
	}

	@Override
	public Object visitNotExpression(NotExpressionContext ctx) {
		Exp negatedExpression = (Exp) visit(ctx.expression());
		return new ExpNeg(negatedExpression);
	}

	@Override
	public Object visitFalseExpression(FalseExpressionContext ctx) {
		return new ExpFalse();
	}

	@Override
	public Object visitNewExpression(NewExpressionContext ctx) {
		String className = ctx.identifier().getText();
		return new ExpNew(className);
	}

	@Override
	public Object visitArrayLengthExpression(ArrayLengthExpressionContext ctx) {
		Exp array = (Exp) visit(ctx.expression());
		return new ExpArrayLength(array);
	}

	@Override
	public Object visitNewIntArrayExpression(NewIntArrayExpressionContext ctx) {
		Exp arraySize = (Exp) visit(ctx.expression());
		return arraySize;
	}
}
