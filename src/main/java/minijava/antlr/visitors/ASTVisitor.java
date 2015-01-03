package minijava.antlr.visitors;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;

import minijava.MiniJavaBaseVisitor;
import minijava.MiniJavaParser;
import minijava.MiniJavaParser.ArrayAccessExpressionContext;
import minijava.MiniJavaParser.ArrayLengthExpressionContext;
import minijava.MiniJavaParser.BinOpExpressionContext;
import minijava.MiniJavaParser.ClassDeclarationContext;
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
import minijava.MiniJavaParser.VarDeclarationContext;
import minijava.ast.rules.declarations.DeclClass;
import minijava.ast.rules.declarations.DeclMain;
import minijava.ast.rules.declarations.DeclMeth;
import minijava.ast.rules.declarations.DeclVar;
import minijava.ast.rules.expressions.Exp;
import minijava.ast.rules.expressions.ExpArrayGet;
import minijava.ast.rules.expressions.ExpArrayLength;
import minijava.ast.rules.expressions.ExpBinOp;
import minijava.ast.rules.expressions.ExpFalse;
import minijava.ast.rules.expressions.ExpId;
import minijava.ast.rules.expressions.ExpIntConst;
import minijava.ast.rules.expressions.ExpInvoke;
import minijava.ast.rules.expressions.ExpNeg;
import minijava.ast.rules.expressions.ExpNew;
import minijava.ast.rules.expressions.ExpNewIntArray;
import minijava.ast.rules.expressions.ExpThis;
import minijava.ast.rules.expressions.ExpTrue;
import minijava.ast.rules.Parameter;
import minijava.ast.rules.Prg;
import minijava.ast.rules.statements.Stm;
import minijava.ast.rules.statements.StmArrayAssign;
import minijava.ast.rules.statements.StmAssign;
import minijava.ast.rules.statements.StmIf;
import minijava.ast.rules.statements.StmList;
import minijava.ast.rules.statements.StmPrintChar;
import minijava.ast.rules.statements.StmPrintlnInt;
import minijava.ast.rules.statements.StmWhile;
import minijava.ast.rules.types.Ty;
import minijava.ast.rules.types.TyArr;
import minijava.ast.rules.types.TyBool;
import minijava.ast.rules.types.TyClass;
import minijava.ast.rules.types.TyInt;
import minijava.ast.rules.expressions.ExpBinOp.Op;

import org.antlr.v4.runtime.misc.NotNull;

public class ASTVisitor extends MiniJavaBaseVisitor<Object> {

	@Override
	public Object visitProg(@NotNull MiniJavaParser.ProgContext ctx) {

		DeclMain mainClass = (DeclMain) visit(ctx.mainClass());
		LinkedList<DeclClass> classes = new LinkedList<>();
		for (ClassDeclarationContext classDeclCtx : ctx.classDeclaration()) {
			classes.add((DeclClass) visit(classDeclCtx));
		}

		return new Prg(mainClass, classes);
	}

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
		for (int i = 2; i < ctx.type().size(); i++) {
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

		return new DeclMeth(ty, methodName, parameters, localVars, new StmList(body), returnExp);
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
		if (ctx.MINUS() != null) {
			binOp = Op.MINUS;
		} else if (ctx.PLUS() != null) {
			binOp = Op.PLUS;
		} else if (ctx.SLASH() != null) {
			binOp =  Op.DIV;
		} else if (ctx.STAR() != null) {
			binOp = Op.TIMES;
		} else if (ctx.SMALLER() != null) {
			binOp = Op.LT;
		} else if (ctx.DOUBLE_AMPERSAND() != null) {
			binOp = Op.AND;
		} else {
			throw new IllegalArgumentException("Unknown operator in: \n"+ctx.getText());
		}
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
		return new ExpNewIntArray(arraySize);
	}
	
	@Override
	public Object visitBracketExpression(@NotNull MiniJavaParser.BracketExpressionContext ctx) {
		return visit(ctx.expression());
	}
}
