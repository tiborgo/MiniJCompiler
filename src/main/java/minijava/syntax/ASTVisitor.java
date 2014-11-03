package minijava.syntax;

import java.util.LinkedList;

import org.antlr.v4.runtime.misc.NotNull;

import minijava.MiniJavaBaseVisitor;
import minijava.MiniJavaParser;
import minijava.MiniJavaParser.StatementContext;
import minijava.MiniJavaParser.VarDeclarationContext;
import minijava.syntax.ast.DeclMeth;
import minijava.syntax.ast.DeclVar;
import minijava.syntax.ast.Exp;
import minijava.syntax.ast.Parameter;
import minijava.syntax.ast.Stm;
import minijava.syntax.ast.Ty;

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
