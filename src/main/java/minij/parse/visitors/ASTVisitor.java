/*
 * MiniJCompiler: Compiler for a subset of the Java(R) language
 *
 * (C) Copyright 2014-2015 Tibor Goldschwendt <goldschwendt@cip.ifi.lmu.de>,
 * Michael Seifert <mseifert@error-reports.org>
 *
 * This file is part of MiniJCompiler.
 *
 * MiniJCompiler is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MiniJCompiler is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MiniJCompiler.  If not, see <http://www.gnu.org/licenses/>.
 */
package minij.parse.visitors;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;

import minij.MiniJBaseVisitor;
import minij.MiniJParser;
import minij.MiniJParser.ArrayAccessExpressionContext;
import minij.MiniJParser.ArrayLengthExpressionContext;
import minij.MiniJParser.BinOpExpressionContext;
import minij.MiniJParser.ClassDeclarationContext;
import minij.MiniJParser.ExpressionContext;
import minij.MiniJParser.FalseExpressionContext;
import minij.MiniJParser.IdentifierExpressionContext;
import minij.MiniJParser.IntegerLiteralExpressionContext;
import minij.MiniJParser.InvokeExpressionContext;
import minij.MiniJParser.MethodDeclarationContext;
import minij.MiniJParser.NewExpressionContext;
import minij.MiniJParser.NewIntArrayExpressionContext;
import minij.MiniJParser.NotExpressionContext;
import minij.MiniJParser.StatementContext;
import minij.MiniJParser.ThisExpressionContext;
import minij.MiniJParser.TrueExpressionContext;
import minij.MiniJParser.VarDeclarationContext;
import minij.parse.rules.Parameter;
import minij.parse.rules.Program;
import minij.parse.rules.declarations.Main;
import minij.parse.rules.declarations.Method;
import minij.parse.rules.declarations.Variable;
import minij.parse.rules.expressions.ArrayGet;
import minij.parse.rules.expressions.ArrayLength;
import minij.parse.rules.expressions.BinOp;
import minij.parse.rules.expressions.Expression;
import minij.parse.rules.expressions.False;
import minij.parse.rules.expressions.Id;
import minij.parse.rules.expressions.IntConstant;
import minij.parse.rules.expressions.Invoke;
import minij.parse.rules.expressions.Negate;
import minij.parse.rules.expressions.New;
import minij.parse.rules.expressions.NewIntArray;
import minij.parse.rules.expressions.This;
import minij.parse.rules.expressions.True;
import minij.parse.rules.expressions.BinOp.Op;
import minij.parse.rules.statements.ArrayAssignment;
import minij.parse.rules.statements.Assignment;
import minij.parse.rules.statements.If;
import minij.parse.rules.statements.PrintChar;
import minij.parse.rules.statements.PrintlnInt;
import minij.parse.rules.statements.Statement;
import minij.parse.rules.statements.StatementList;
import minij.parse.rules.statements.While;
import minij.parse.rules.types.Array;
import minij.parse.rules.types.Boolean;
import minij.parse.rules.types.Class;
import minij.parse.rules.types.Integer;
import minij.parse.rules.types.Type;

import org.antlr.v4.runtime.misc.NotNull;

public class ASTVisitor extends MiniJBaseVisitor<Object> {

	@Override
	public Object visitProg(@NotNull MiniJParser.ProgContext ctx) {

		LinkedList<minij.parse.rules.declarations.Class> classes = new LinkedList<>();
		for (ClassDeclarationContext classDeclCtx : ctx.classDeclaration()) {
			classes.add((minij.parse.rules.declarations.Class) visit(classDeclCtx));
		}
		
		Main mainClass = (Main) visit(ctx.mainClass());
		classes.add(mainClass);

		return new Program(classes);
	}

	@Override
	public Object visitIdentifier(@NotNull MiniJParser.IdentifierContext ctx) {
		return ctx.IDENTIFIER().getText();
	}

	@Override
	public Object visitMethodDeclaration(@NotNull MiniJParser.MethodDeclarationContext ctx) {

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
	public Object visitMainClass(@NotNull MiniJParser.MainClassContext ctx) {
		String className = ctx.identifier(0).getText();
		String mainMethodArgumentVariableName = ctx.identifier(1).getText();
		Statement statement = (Statement) visit(ctx.statement());
		return new Main(className, mainMethodArgumentVariableName, statement);
	}

	/* ####### TYPES ####### */

	@Override
	public Object visitIntType(@NotNull MiniJParser.IntTypeContext ctx) {
		return new Integer();
	}

	@Override
	public Object visitIntArrayType(@NotNull MiniJParser.IntArrayTypeContext ctx) {
		return new Array(new Integer());
	}

	@Override
	public Object visitBooleanType(@NotNull MiniJParser.BooleanTypeContext ctx) {
		return new Boolean();
	}

	@Override
	public Object visitOtherType(@NotNull MiniJParser.OtherTypeContext ctx) {

		String name = (String) visit(ctx.identifier());
		return new Class(name);
	}

	/* ####### STATEMENTS ####### */

	@Override
	public Object visitBracketStatement(@NotNull MiniJParser.BracketStatementContext ctx) {

		LinkedList<Statement> statements = new LinkedList<>();
		for (StatementContext stmCtx : ctx.statement()) {
			statements.add((Statement) visit(stmCtx));
		}

		return new StatementList(statements);
	}

	@Override
	public Object visitIfStatement(@NotNull MiniJParser.IfStatementContext ctx) {

		Expression cond = (Expression) visit(ctx.condition);
		Statement bodyTrue = (Statement) visit(ctx.trueStatement);
		Statement bodyFalse = (Statement) visit(ctx.falseStatement);

		return new If(cond, bodyTrue, bodyFalse);
	}

	@Override
	public Object visitWhileStatement(@NotNull MiniJParser.WhileStatementContext ctx) {

		Expression cond = (Expression) visit(ctx.expression());
		Statement body = (Statement) visit(ctx.statement());

		return new While(cond, body);
	}

	@Override
	public Object visitSystemOutPrintlnStatement(@NotNull MiniJParser.SystemOutPrintlnStatementContext ctx) {

		Expression arg = (Expression) visit(ctx.expression());

		return new PrintlnInt(arg);
	}

	@Override
	public Object visitAssignStatement(@NotNull MiniJParser.AssignStatementContext ctx) {

		Id id = new Id((String) visit(ctx.identifier()));
		Expression rhs = (Expression) visit(ctx.expression());

		return new Assignment(id, rhs);
	}

	@Override
	public Object visitSystemOutPrintStatement(@NotNull MiniJParser.SystemOutPrintStatementContext ctx) {

		Expression arg = (Expression) visit(ctx.expression());

		return new PrintChar(arg);
	}

	@Override
	public Object visitArrayAssignStatement(@NotNull MiniJParser.ArrayAssignStatementContext ctx) {

		Id id = new Id((String) visit(ctx.identifier()));
		Expression index = (Expression) visit(ctx.index);
		Expression rhs = (Expression) visit(ctx.rhs);

		return new ArrayAssignment(id, index, rhs);
	}

	@Override
	public Object visitVarDeclaration(@NotNull MiniJParser.VarDeclarationContext ctx) {

		Type type = (Type) visit(ctx.type());
		String name = (String) visit(ctx.identifier());

		return new Variable(type, name);
	}

	@Override
	public Object visitClassDeclaration(@NotNull MiniJParser.ClassDeclarationContext ctx) {
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
		return new minij.parse.rules.declarations.Class(className, superClassName, fields, methods);
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
	public Object visitBracketExpression(@NotNull MiniJParser.BracketExpressionContext ctx) {
		return visit(ctx.expression());
	}
}
