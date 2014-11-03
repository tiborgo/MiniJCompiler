package minijava.syntax;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;

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
import minijava.MiniJavaParser.MethodDeclarationContext;
import minijava.MiniJavaParser.NewExpressionContext;
import minijava.MiniJavaParser.NewIntArrayExpressionContext;
import minijava.MiniJavaParser.NotExpressionContext;
import minijava.MiniJavaParser.StatementContext;
import minijava.MiniJavaParser.ThisExpressionContext;
import minijava.MiniJavaParser.TrueExpressionContext;
import minijava.syntax.ast.DeclMain;
import minijava.syntax.ast.DeclMeth;
import minijava.syntax.ast.DeclVar;
import minijava.syntax.ast.Exp;
import minijava.syntax.ast.ExpArrayGet;
import minijava.syntax.ast.ExpArrayLength;
import minijava.syntax.ast.ExpBinOp;
import minijava.syntax.ast.ExpBinOp.Op;
import minijava.syntax.ast.DeclClass;
import minijava.syntax.ast.ExpFalse;
import minijava.syntax.ast.ExpId;
import minijava.syntax.ast.ExpIntConst;
import minijava.syntax.ast.ExpInvoke;
import minijava.syntax.ast.ExpNeg;
import minijava.syntax.ast.ExpNew;
import minijava.syntax.ast.ExpThis;
import minijava.syntax.ast.ExpTrue;
import minijava.MiniJavaParser.VarDeclarationContext;
import minijava.syntax.ast.Parameter;
import minijava.syntax.ast.Stm;
import minijava.syntax.ast.StmArrayAssign;
import minijava.syntax.ast.StmAssign;
import minijava.syntax.ast.StmIf;
import minijava.syntax.ast.StmList;
import minijava.syntax.ast.StmPrintChar;
import minijava.syntax.ast.StmPrintlnInt;
import minijava.syntax.ast.StmWhile;
import minijava.syntax.ast.Ty;
import minijava.syntax.ast.TyArr;
import minijava.syntax.ast.TyBool;
import minijava.syntax.ast.TyClass;
import minijava.syntax.ast.TyInt;

import org.antlr.v4.runtime.misc.NotNull;

public class ASTVisitor extends MiniJavaBaseVisitor<Object> {

	@Override
	public Object visitIdentifier(@NotNull MiniJavaParser.IdentifierContext ctx) {
		return ctx.IDENTIFIER().getText();
	}

	@Override
	public Object visitMethodDeclaration(@NotNull MiniJavaParser.MethodDeclarationContext ctx) {

		Ty ty = (Ty) visit(ctx.returnType);
		String methodName = (String) visit(ctx.methodName);

		LinkedList<Parameter> parameters = new LinkedList<>();
		if (ctx.firstParameterType != null) {
			parameters.add(new Parameter((String) visit(ctx.firstParameterName), (Ty) visit(ctx.firstParameterType)));
		}
		for (int i = 0; i < ctx.type().size(); i++) {
			parameters.add(new Parameter((String) visit(ctx.identifier().get(i)), (Ty) visit(ctx.type().get(i))));
		}

		LinkedList<DeclVar> localVars = new LinkedList<>();
		for (VarDeclarationContext varDeclarationCtx : ctx.varDeclaration()) {
			localVars.add((DeclVar) visit(varDeclarationCtx));
		}

		LinkedList<Stm> body = new LinkedList<>();
		for (StatementContext stmDeclCtx : ctx.statement()) {
			body.add((Stm) visit(stmDeclCtx));
		}

		Exp returnExp = (Exp) visit(ctx.returnExpression);

		return new DeclMeth(ty, methodName, parameters, localVars, body, returnExp);
	}

	@Override
	public Object visitMainClass(@NotNull MiniJavaParser.MainClassContext ctx) {
		String className = ctx.identifier(0).getText();
		String mainMethodArgumentVariableName = ctx.identifier(1).getText();
		Stm statement = (Stm) visit(ctx.statement());
		return new DeclMain(className, mainMethodArgumentVariableName, statement);
	}

	/* ####### TYPES ####### */

	@Override
	public Object visitIntType(@NotNull MiniJavaParser.IntTypeContext ctx) {
		return new TyInt();
	}

	@Override
	public Object visitIntArrayType(@NotNull MiniJavaParser.IntArrayTypeContext ctx) {
		return new TyArr(new TyInt());
	}

	@Override
	public Object visitBooleanType(@NotNull MiniJavaParser.BooleanTypeContext ctx) {
		return new TyBool();
	}

	@Override
	public Object visitOtherType(@NotNull MiniJavaParser.OtherTypeContext ctx) {

		String name = (String) visit(ctx.identifier());
		return new TyClass(name);
	}

	/* ####### STATEMENTS ####### */

	@Override
	public Object visitBracketStatement(@NotNull MiniJavaParser.BracketStatementContext ctx) {

		LinkedList<Stm> stms = new LinkedList<>();
		for (StatementContext stmCtx : ctx.statement()) {
			stms.add((Stm) visit(stmCtx));
		}

		return new StmList(stms);
	}

	@Override
	public Object visitIfStatement(@NotNull MiniJavaParser.IfStatementContext ctx) {

		Exp cond = (Exp) visit(ctx.condition);
		Stm bodyTrue = (Stm) visit(ctx.trueStatement);
		Stm bodyFalse = (Stm) visit(ctx.falseStatement);

		return new StmIf(cond, bodyTrue, bodyFalse);
	}

	@Override
	public Object visitWhileStatement(@NotNull MiniJavaParser.WhileStatementContext ctx) {

		Exp cond = (Exp) visit(ctx.expression());
		Stm body = (Stm) visit(ctx.statement());

		return new StmWhile(cond, body);
	}

	@Override
	public Object visitSystemOutPrintlnStatement(@NotNull MiniJavaParser.SystemOutPrintlnStatementContext ctx) {

		Exp arg = (Exp) visit(ctx.expression());

		return new StmPrintlnInt(arg);
	}

	@Override
	public Object visitAssignStatement(@NotNull MiniJavaParser.AssignStatementContext ctx) {

		String id = (String) visit(ctx.identifier());
		Exp rhs = (Exp) visit(ctx.expression());

		return new StmAssign(id, rhs);
	}

	@Override
	public Object visitSystemOutPrintStatement(@NotNull MiniJavaParser.SystemOutPrintStatementContext ctx) {

		Exp arg = (Exp) visit(ctx.expression());

		return new StmPrintChar(arg);
	}

	@Override
	public Object visitArrayAssignStatement(@NotNull MiniJavaParser.ArrayAssignStatementContext ctx) {

		String id = (String) visit(ctx.identifier());
		Exp index = (Exp) visit(ctx.index);
		Exp rhs = (Exp) visit(ctx.rhs);

		return new StmArrayAssign(id, index, rhs);
	}

	@Override
	public Object visitVarDeclaration(@NotNull MiniJavaParser.VarDeclarationContext ctx) {

		Ty ty = (Ty) visit(ctx.type());
		String name = (String) visit(ctx.identifier());

		return new DeclVar(ty, name);
	}

	@Override
	public Object visitClassDeclaration(@NotNull MiniJavaParser.ClassDeclarationContext ctx) {
		String className = ctx.className.getText();
		String superClassName = ctx.superClassName != null ? ctx.superClassName.getText() : null;
		List<VarDeclarationContext> fieldsRaw = ctx.varDeclaration();
		List<DeclVar> fields = new ArrayList<>(fieldsRaw.size());
		for (VarDeclarationContext fieldRaw : fieldsRaw) {
			DeclVar field = (DeclVar) visit(fieldRaw);
			fields.add(field);
		}
		List<MethodDeclarationContext> methodsRaw = ctx.methodDeclaration();
		List<DeclMeth> methods = new ArrayList<>(methodsRaw.size());
		for (MethodDeclarationContext methodRaw : methodsRaw) {
			DeclMeth method = (DeclMeth) visit(methodRaw);
			methods.add(method);
		}
		return new DeclClass(className, superClassName, fields, methods);
	}

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
		List<ExpressionContext> argumentsRaw = ctx.expression().subList(1, ctx.expression().size());
		List<Exp> arguments = new ArrayList<>(argumentsRaw.size());
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
