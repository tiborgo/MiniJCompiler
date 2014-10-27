// Generated from MiniJava.g4 by ANTLR 4.4
import minijava.MiniJavaBaseVisitor;
import minijava.MiniJavaParser;

import org.antlr.v4.runtime.misc.NotNull;

public class MiniJavaPrettyPrintVisitor extends MiniJavaBaseVisitor<String> {

	private int level = 0;

	private String getTabs() {
		String tabs = "";
		for (int i = 0; i < level; i++) {
			tabs += '\t';
		}
		return tabs;
	}

	@Override
	public String visitExpression(@NotNull MiniJavaParser.ExpressionContext ctx) {
		return visitChildren(ctx);
	}

	@Override
	public String visitProg(@NotNull MiniJavaParser.ProgContext ctx) {

		// prog: mainClass ( classDeclaration )* EOF;

		String code = visit(ctx.mainClass());
		for (MiniJavaParser.ClassDeclarationContext childCtx : ctx
				.classDeclaration()) {
			code += visit(childCtx);
		}

		return code;
	}

	@Override
	public String visitMainClass(@NotNull MiniJavaParser.MainClassContext ctx) {

		level++;

		level++;
		String statements = visit(ctx.statement());
		level--;

		String code = "class " + visit(ctx.identifier(0)) + "{\n" + getTabs()
				+ "public static void main(String[] "
				+ visit(ctx.identifier(1)) + ") {\n" + statements + getTabs()
				+ "}\n" + "}\n";
		level--;

		return code;
	}

	@Override
	public String visitClassDeclarationSimple(
			@NotNull MiniJavaParser.ClassDeclarationSimpleContext ctx) {
		// CLASS identifier classBody;
		return getTabs() + "class " + visit(ctx.identifier()) + " "
				+ visit(ctx.classBody());
	}

	@Override
	public String visitClassDeclarationExtends(
			@NotNull MiniJavaParser.ClassDeclarationExtendsContext ctx) {
		return visitChildren(ctx);
	}

	@Override
	public String visitClassBody(@NotNull MiniJavaParser.ClassBodyContext ctx) {
		// LCBRACKET ( varDeclaration )* ( methodDeclaration )* RCBRACKET;
		String code = "{\n";
		level++;
		for (MiniJavaParser.VarDeclarationContext childCtx : ctx
				.varDeclaration()) {
			code += visit(childCtx);
		}
		for (MiniJavaParser.MethodDeclarationContext childCtx : ctx
				.methodDeclaration()) {
			code += visit(childCtx);
		}
		level--;
		code += getTabs() + "}\n";

		return code;
	}

	@Override
	public String visitIdentifier(@NotNull MiniJavaParser.IdentifierContext ctx) {
		return ctx.IDENTIFIER().getText();
	}

	@Override
	public String visitMethodDeclaration(
			@NotNull MiniJavaParser.MethodDeclarationContext ctx) {
		// methodDeclaration: PUBLIC type identifier LBRACKET ( type identifier
		// ( COMMA type identifier )* )? RBRACKET LCBRACKET ( varDeclaration )*
		// ( statement )* RETURN expression SEMICOLON RCBRACKET;
		String code = getTabs() + "public " + visit(ctx.type(0)) + " "
				+ visit(ctx.identifier(0)) + "(";
		if (ctx.type(1) != null) {
			code += visit(ctx.type(1)) + " " + visit(ctx.identifier(1));
			for (int i = 0; i < ctx.type().size(); i++) {
				code += ", " + visit(ctx.type().get(i)) + " "
						+ visit(ctx.identifier().get(i));
			}
		}
		code += ") {\n";
		level++;
		for (MiniJavaParser.VarDeclarationContext childCtx : ctx
				.varDeclaration()) {
			code += visit(childCtx);
		}
		for (MiniJavaParser.StatementContext childCtx : ctx.statement()) {
			code += visit(childCtx);
		}
		code += getTabs() + "return " + visit(ctx.expression()) + ";\n";
		level--;
		code += getTabs() + "}\n";

		return code;
	}

	@Override
	public String visitVarDeclaration(
			@NotNull MiniJavaParser.VarDeclarationContext ctx) {
		// varDeclaration: type identifier SEMICOLON;
		return getTabs() + visit(ctx.type()) + " " + visit(ctx.identifier())
				+ ";\n";
	}

	@Override
	public String visitArrayAssignStatement(
			@NotNull MiniJavaParser.ArrayAssignStatementContext ctx) {

		// identifier LSBRACKET expression RSBRACKET EQUAL_SIGN expression
		// SEMICOLON;
		return getTabs() + visit(ctx.identifier()) + "["
				+ visit(ctx.expression(0)) + "] = " + visit(ctx.expression(1))
				+ ";\n";
	}

	@Override
	public String visitAssignStatement(
			@NotNull MiniJavaParser.AssignStatementContext ctx) {
		// assignStatement: identifier EQUAL_SIGN expression SEMICOLON;
		return getTabs() + visit(ctx.identifier()) + " = "
				+ visit(ctx.expression()) + ";\n";
	}

	@Override
	public String visitSystemOutPrintlnStatement(
			@NotNull MiniJavaParser.SystemOutPrintlnStatementContext ctx) {
		// SystemOutPrintlnStatement: SYSTEM_OUT_PRINTLN LBRACKET expression
		// RBRACKET SEMICOLON;
		return getTabs() + "System.out.println(" + visit(ctx.expression())
				+ ");\n";
	}

	@Override
	public String visitIfStatement(
			@NotNull MiniJavaParser.IfStatementContext ctx) {
		// IF LBRACKET expression RBRACKET statement ELSE statement
		String code = "if (" + visit(ctx.expression()) + ")\n";
		level++;
		code += visit(ctx.statement(0));
		level--;
		code += "else\n";
		level++;
		code += visit(ctx.statement(1));
		level--;
		return code;
	}

	@Override
	public String visitWhileStatement(
			@NotNull MiniJavaParser.WhileStatementContext ctx) {
		// WHILE LBRACKET expression RBRACKET statement
		String code = getTabs() + "while(" + visit(ctx.expression()) + ") "
				+ visit(ctx.statement());
		return code;
	}

	@Override
	public String visitBracketStatement(
			@NotNull MiniJavaParser.BracketStatementContext ctx) {
		// LCBRACKET ( statement )* RCBRACKET
		String code = getTabs() + "{\n";
		level++;
		for (MiniJavaParser.StatementContext childCtx : ctx.statement()) {
			code += visit(childCtx);
		}
		level--;
		code += getTabs() + "}\n";
		return code;
	}

	@Override
	public String visitSystemOutPrintStatement(
			@NotNull MiniJavaParser.SystemOutPrintStatementContext ctx) {
		// systemOutPrintStatement: SYSTEM_OUT_PRINT LBRACKET LBRACKET CHAR
		// RBRACKET expression RBRACKET SEMICOLON;
		return getTabs() + "System.out.print((char)" + visit(ctx.expression())
				+ ");\n";
	}

	@Override
	public String visitIntType(@NotNull MiniJavaParser.IntTypeContext ctx) {
		return "int";
	}

	@Override
	public String visitIntArrayType(
			@NotNull MiniJavaParser.IntArrayTypeContext ctx) {
		return "int[]";
	}

	@Override
	public String visitBooleanType(
			@NotNull MiniJavaParser.BooleanTypeContext ctx) {
		return "boolean";
	}

	@Override
	public String visitOtherType(@NotNull MiniJavaParser.OtherTypeContext ctx) {
		return visit(ctx.identifier());
	}
}