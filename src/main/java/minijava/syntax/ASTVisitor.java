package minijava.syntax;

import org.antlr.v4.runtime.misc.NotNull;

import minijava.MiniJavaBaseVisitor;
import minijava.MiniJavaParser;

public class ASTVisitor extends MiniJavaBaseVisitor<Object> {

	@Override public Object visitIdentifier(@NotNull MiniJavaParser.IdentifierContext ctx) { return visitChildren(ctx); }

	@Override public Object visitMethodDeclaration(@NotNull MiniJavaParser.MethodDeclarationContext ctx) { return visitChildren(ctx); }

	@Override public Object visitBooleanType(@NotNull MiniJavaParser.BooleanTypeContext ctx) { return visitChildren(ctx); }

	@Override public Object visitExpression(@NotNull MiniJavaParser.ExpressionContext ctx) { return visitChildren(ctx); }

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
}
