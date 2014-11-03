package minijava;

import minijava.MiniJavaParser.ClassBodyContext;
import minijava.MiniJavaParser.ClassDeclarationExtendsContext;
import minijava.MiniJavaParser.ClassDeclarationSimpleContext;
import minijava.MiniJavaParser.MethodDeclarationContext;
import minijava.MiniJavaParser.OtherTypeContext;
import minijava.MiniJavaParser.VarDeclarationContext;

public class SymbolTableVisitor extends MiniJavaBaseVisitor<SymbolTable> {
	private final Program program;
	private Class currentClass;

	public SymbolTableVisitor() {
		program = new Program();
		Class intClass = new Class("int");
		program.add(intClass);
		Class intArrayClass = new Class("int[]");
		program.add(intArrayClass);
		Class booleanClass = new Class("boolean");
		program.add(booleanClass);
	}

	@Override
	public SymbolTable visitVarDeclaration(VarDeclarationContext ctx) {

		return super.visitVarDeclaration(ctx);
	}

	@Override
	public SymbolTable visitClassDeclarationExtends(ClassDeclarationExtendsContext ctx) {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public SymbolTable visitClassDeclarationSimple(ClassDeclarationSimpleContext ctx) {
		String className = ctx.identifier().IDENTIFIER().getText();
		if (program.contains(className)) {
			System.out.println("Class "+className+" has already been declared.");
		} else {
			Class clazz = new Class(className);
			currentClass = clazz;
			program.add(clazz);
		}
		return visit(ctx.classBody());
	}

	@Override
	public SymbolTable visitClassBody(ClassBodyContext ctx) {
		for (VarDeclarationContext varDeclaration : ctx.varDeclaration()) {
			if (varDeclaration.type() instanceof OtherTypeContext) {
				OtherTypeContext typeContext = (OtherTypeContext) varDeclaration.type();
				String typeName = typeContext.identifier().IDENTIFIER().getText();
				String variableName = varDeclaration.identifier().IDENTIFIER().getText();
				currentClass.fields.put(variableName, typeName);
			}
		}

		for (MethodDeclarationContext methodDeclaration : ctx.methodDeclaration()) {
			visit(methodDeclaration);
		}
		return super.visitClassBody(ctx);
	}
}
