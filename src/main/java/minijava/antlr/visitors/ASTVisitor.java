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
import minijava.ast.rules.declarations.Class;
import minijava.ast.rules.declarations.Main;
import minijava.ast.rules.declarations.Method;
import minijava.ast.rules.declarations.Variable;
import minijava.ast.rules.expressions.Expression;
import minijava.ast.rules.expressions.ArrayGet;
import minijava.ast.rules.expressions.ArrayLength;
import minijava.ast.rules.expressions.BinOp;
import minijava.ast.rules.expressions.False;
import minijava.ast.rules.expressions.Id;
import minijava.ast.rules.expressions.IntConstant;
import minijava.ast.rules.expressions.Invoke;
import minijava.ast.rules.expressions.Negate;
import minijava.ast.rules.expressions.New;
import minijava.ast.rules.expressions.NewIntArray;
import minijava.ast.rules.expressions.This;
import minijava.ast.rules.expressions.True;
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
import minijava.ast.rules.expressions.BinOp.Op;

import org.antlr.v4.runtime.misc.NotNull;

public class ASTVisitor extends MiniJavaBaseVisitor<Object> {

	@Override
	public Object visitProg(@NotNull MiniJavaParser.ProgContext ctx) {

		Main mainClass = (Main) visit(ctx.mainClass());
		LinkedList<Class> classes = new LinkedList<>();
		for (ClassDeclarationContext classDeclCtx : ctx.classDeclaration()) {
			classes.add((Class) visit(classDeclCtx));
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

		LinkedList<Variable> localVars = new LinkedList<>();
		for (VarDeclarationContext varDeclarationCtx : ctx.varDeclaration()) {
			localVars.add((Variable) visit(varDeclarationCtx));
		}

		LinkedList<Stm> body = new LinkedList<>();
		for (StatementContext stmDeclCtx : ctx.statement()) {
			body.add((Stm) visit(stmDeclCtx));
		}

		Expression returnExpression = (Expression) visit(ctx.returnExpression);

		return new Method(ty, methodName, parameters, localVars, new StmList(body), returnExpression);
	}

	@Override
	public Object visitMainClass(@NotNull MiniJavaParser.MainClassContext ctx) {
		String className = ctx.identifier(0).getText();
		String mainMethodArgumentVariableName = ctx.identifier(1).getText();
		Stm statement = (Stm) visit(ctx.statement());
		return new Main(className, mainMethodArgumentVariableName, statement);
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

		Expression cond = (Expression) visit(ctx.condition);
		Stm bodyTrue = (Stm) visit(ctx.trueStatement);
		Stm bodyFalse = (Stm) visit(ctx.falseStatement);

		return new StmIf(cond, bodyTrue, bodyFalse);
	}

	@Override
	public Object visitWhileStatement(@NotNull MiniJavaParser.WhileStatementContext ctx) {

		Expression cond = (Expression) visit(ctx.expression());
		Stm body = (Stm) visit(ctx.statement());

		return new StmWhile(cond, body);
	}

	@Override
	public Object visitSystemOutPrintlnStatement(@NotNull MiniJavaParser.SystemOutPrintlnStatementContext ctx) {

		Expression arg = (Expression) visit(ctx.expression());

		return new StmPrintlnInt(arg);
	}

	@Override
	public Object visitAssignStatement(@NotNull MiniJavaParser.AssignStatementContext ctx) {

		String id = (String) visit(ctx.identifier());
		Expression rhs = (Expression) visit(ctx.expression());

		return new StmAssign(id, rhs);
	}

	@Override
	public Object visitSystemOutPrintStatement(@NotNull MiniJavaParser.SystemOutPrintStatementContext ctx) {

		Expression arg = (Expression) visit(ctx.expression());

		return new StmPrintChar(arg);
	}

	@Override
	public Object visitArrayAssignStatement(@NotNull MiniJavaParser.ArrayAssignStatementContext ctx) {

		String id = (String) visit(ctx.identifier());
		Expression index = (Expression) visit(ctx.index);
		Expression rhs = (Expression) visit(ctx.rhs);

		return new StmArrayAssign(id, index, rhs);
	}

	@Override
	public Object visitVarDeclaration(@NotNull MiniJavaParser.VarDeclarationContext ctx) {

		Ty ty = (Ty) visit(ctx.type());
		String name = (String) visit(ctx.identifier());

		return new Variable(ty, name);
	}

	@Override
	public Object visitClassDeclaration(@NotNull MiniJavaParser.ClassDeclarationContext ctx) {
		String className = ctx.className.getText();
		String superClassName = ctx.superClassName != null ? ctx.superClassName.getText() : null;
		List<VarDeclarationContext> fieldsRaw = ctx.varDeclaration();
		List<Variable> fields = new ArrayList<>(fieldsRaw.size());
		for (VarDeclarationContext fieldRaw : fieldsRaw) {
			Variable field = (Variable) visit(fieldRaw);
			fields.add(field);
		}
		List<MethodDeclarationContext> methodsRaw = ctx.methodDeclaration();
		List<Method> methods = new ArrayList<>(methodsRaw.size());
		for (MethodDeclarationContext methodRaw : methodsRaw) {
			Method method = (Method) visit(methodRaw);
			methods.add(method);
		}
		return new Class(className, superClassName, fields, methods);
	}

	@Override
	public Object visitThisExpression(ThisExpressionContext ctx) {
		return new This();
	}

	@Override
	public Object visitBinOpExpression(BinOpExpressionContext ctx) {
		BinOp.Op binOp;
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
		Expression expression0 = (Expression) visit(ctx.expression(0));
		Expression expression1 = (Expression) visit(ctx.expression(1));
		return new BinOp(expression0, binOp, expression1);
	}

	@Override
	public Object visitIntegerLiteralExpression(IntegerLiteralExpressionContext ctx) {
		Integer value = Integer.parseInt(ctx.INTEGER_LITERAL().getText());
		return new IntConstant(value);
	}

	@Override
	public Object visitArrayAccessExpression(ArrayAccessExpressionContext ctx) {
		Expression arrayExpression = (Expression) visit(ctx.expression(0));
		Expression indexExpression = (Expression) visit(ctx.expression(1));
		return new ArrayGet(arrayExpression, indexExpression);
	}

	@Override
	public Object visitTrueExpression(TrueExpressionContext ctx) {
		return new True();
	}

	@Override
	public Object visitInvokeExpression(InvokeExpressionContext ctx) {
		Expression object = (Expression) visit(ctx.expression(0));
		String method = ctx.identifier().getText();
		List<ExpressionContext> argumentsRaw = ctx.expression().subList(1, ctx.expression().size());
		List<Expression> arguments = new ArrayList<>(argumentsRaw.size());
		for (ExpressionContext argumentRaw : argumentsRaw) {
			Expression argument = (Expression) visit(argumentRaw);
			arguments.add(argument);
		}
		return new Invoke(object, method, arguments);
	}

	@Override
	public Object visitIdentifierExpression(IdentifierExpressionContext ctx) {
		String id = ctx.identifier().getText();
		return new Id(id);
	}

	@Override
	public Object visitNotExpression(NotExpressionContext ctx) {
		Expression negatedExpression = (Expression) visit(ctx.expression());
		return new Negate(negatedExpression);
	}

	@Override
	public Object visitFalseExpression(FalseExpressionContext ctx) {
		return new False();
	}

	@Override
	public Object visitNewExpression(NewExpressionContext ctx) {
		String className = ctx.identifier().getText();
		return new New(className);
	}

	@Override
	public Object visitArrayLengthExpression(ArrayLengthExpressionContext ctx) {
		Expression array = (Expression) visit(ctx.expression());
		return new ArrayLength(array);
	}

	@Override
	public Object visitNewIntArrayExpression(NewIntArrayExpressionContext ctx) {
		Expression arraySize = (Expression) visit(ctx.expression());
		return new NewIntArray(arraySize);
	}
	
	@Override
	public Object visitBracketExpression(@NotNull MiniJavaParser.BracketExpressionContext ctx) {
		return visit(ctx.expression());
	}
}
