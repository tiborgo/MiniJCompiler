package minijava.parse.visitors;

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
import minijava.parse.rules.Parameter;
import minijava.parse.rules.Program;
import minijava.parse.rules.declarations.Main;
import minijava.parse.rules.declarations.Method;
import minijava.parse.rules.declarations.Variable;
import minijava.parse.rules.expressions.ArrayGet;
import minijava.parse.rules.expressions.ArrayLength;
import minijava.parse.rules.expressions.BinOp;
import minijava.parse.rules.expressions.Expression;
import minijava.parse.rules.expressions.False;
import minijava.parse.rules.expressions.Id;
import minijava.parse.rules.expressions.IntConstant;
import minijava.parse.rules.expressions.Invoke;
import minijava.parse.rules.expressions.Negate;
import minijava.parse.rules.expressions.New;
import minijava.parse.rules.expressions.NewIntArray;
import minijava.parse.rules.expressions.This;
import minijava.parse.rules.expressions.True;
import minijava.parse.rules.expressions.BinOp.Op;
import minijava.parse.rules.statements.ArrayAssignment;
import minijava.parse.rules.statements.Assignment;
import minijava.parse.rules.statements.If;
import minijava.parse.rules.statements.PrintChar;
import minijava.parse.rules.statements.PrintlnInt;
import minijava.parse.rules.statements.Statement;
import minijava.parse.rules.statements.StatementList;
import minijava.parse.rules.statements.While;
import minijava.parse.rules.types.Array;
import minijava.parse.rules.types.Boolean;
import minijava.parse.rules.types.Class;
import minijava.parse.rules.types.Integer;
import minijava.parse.rules.types.Type;

import org.antlr.v4.runtime.misc.NotNull;

public class ASTVisitor extends MiniJavaBaseVisitor<Object> {

	@Override
	public Object visitProg(@NotNull MiniJavaParser.ProgContext ctx) {

		LinkedList<minijava.parse.rules.declarations.Class> classes = new LinkedList<>();
		for (ClassDeclarationContext classDeclCtx : ctx.classDeclaration()) {
			classes.add((minijava.parse.rules.declarations.Class) visit(classDeclCtx));
		}
		
		Main mainClass = (Main) visit(ctx.mainClass());
		classes.add(mainClass);

		return new Program(classes);
	}

	@Override
	public Object visitIdentifier(@NotNull MiniJavaParser.IdentifierContext ctx) {
		return ctx.IDENTIFIER().getText();
	}

	@Override
	public Object visitMethodDeclaration(@NotNull MiniJavaParser.MethodDeclarationContext ctx) {

		Type type = (Type) visit(ctx.returnType);
		String methodName = (String) visit(ctx.methodName);

		LinkedList<Parameter> parameters = new LinkedList<>();
		if (ctx.firstParameterType != null) {
			parameters.add(new Parameter((String) visit(ctx.firstParameterName), (Type) visit(ctx.firstParameterType)));
		}
		for (int i = 2; i < ctx.type().size(); i++) {
			parameters.add(new Parameter((String) visit(ctx.identifier().get(i)), (Type) visit(ctx.type().get(i))));
		}

		LinkedList<Variable> localVars = new LinkedList<>();
		for (VarDeclarationContext varDeclarationCtx : ctx.varDeclaration()) {
			localVars.add((Variable) visit(varDeclarationCtx));
		}

		LinkedList<Statement> body = new LinkedList<>();
		for (StatementContext stmDeclCtx : ctx.statement()) {
			body.add((Statement) visit(stmDeclCtx));
		}

		Expression returnExpression = (Expression) visit(ctx.returnExpression);

		return new Method(type, methodName, parameters, localVars, new StatementList(body), returnExpression);
	}

	@Override
	public Object visitMainClass(@NotNull MiniJavaParser.MainClassContext ctx) {
		String className = ctx.identifier(0).getText();
		String mainMethodArgumentVariableName = ctx.identifier(1).getText();
		Statement statement = (Statement) visit(ctx.statement());
		return new Main(className, mainMethodArgumentVariableName, statement);
	}

	/* ####### TYPES ####### */

	@Override
	public Object visitIntType(@NotNull MiniJavaParser.IntTypeContext ctx) {
		return new Integer();
	}

	@Override
	public Object visitIntArrayType(@NotNull MiniJavaParser.IntArrayTypeContext ctx) {
		return new Array(new Integer());
	}

	@Override
	public Object visitBooleanType(@NotNull MiniJavaParser.BooleanTypeContext ctx) {
		return new Boolean();
	}

	@Override
	public Object visitOtherType(@NotNull MiniJavaParser.OtherTypeContext ctx) {

		String name = (String) visit(ctx.identifier());
		return new Class(name);
	}

	/* ####### STATEMENTS ####### */

	@Override
	public Object visitBracketStatement(@NotNull MiniJavaParser.BracketStatementContext ctx) {

		LinkedList<Statement> statements = new LinkedList<>();
		for (StatementContext stmCtx : ctx.statement()) {
			statements.add((Statement) visit(stmCtx));
		}

		return new StatementList(statements);
	}

	@Override
	public Object visitIfStatement(@NotNull MiniJavaParser.IfStatementContext ctx) {

		Expression cond = (Expression) visit(ctx.condition);
		Statement bodyTrue = (Statement) visit(ctx.trueStatement);
		Statement bodyFalse = (Statement) visit(ctx.falseStatement);

		return new If(cond, bodyTrue, bodyFalse);
	}

	@Override
	public Object visitWhileStatement(@NotNull MiniJavaParser.WhileStatementContext ctx) {

		Expression cond = (Expression) visit(ctx.expression());
		Statement body = (Statement) visit(ctx.statement());

		return new While(cond, body);
	}

	@Override
	public Object visitSystemOutPrintlnStatement(@NotNull MiniJavaParser.SystemOutPrintlnStatementContext ctx) {

		Expression arg = (Expression) visit(ctx.expression());

		return new PrintlnInt(arg);
	}

	@Override
	public Object visitAssignStatement(@NotNull MiniJavaParser.AssignStatementContext ctx) {

		// FIXME: Change grammar so it yields an Id expression
		Id id = new Id((String) visit(ctx.identifier()));
		Expression rhs = (Expression) visit(ctx.expression());

		return new Assignment(id, rhs);
	}

	@Override
	public Object visitSystemOutPrintStatement(@NotNull MiniJavaParser.SystemOutPrintStatementContext ctx) {

		Expression arg = (Expression) visit(ctx.expression());

		return new PrintChar(arg);
	}

	@Override
	public Object visitArrayAssignStatement(@NotNull MiniJavaParser.ArrayAssignStatementContext ctx) {

		// FIXME: Change grammar so it yields an Id expression
		Id id = new Id((String) visit(ctx.identifier()));
		Expression index = (Expression) visit(ctx.index);
		Expression rhs = (Expression) visit(ctx.rhs);

		return new ArrayAssignment(id, index, rhs);
	}

	@Override
	public Object visitVarDeclaration(@NotNull MiniJavaParser.VarDeclarationContext ctx) {

		Type type = (Type) visit(ctx.type());
		String name = (String) visit(ctx.identifier());

		return new Variable(type, name);
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
		return new minijava.parse.rules.declarations.Class(className, superClassName, fields, methods);
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
		java.lang.Integer value = java.lang.Integer.parseInt(ctx.INTEGER_LITERAL().getText());
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
