package minijava;

import java.util.LinkedList;
import java.util.List;

import org.antlr.v4.runtime.ParserRuleContext;

import minijava.MiniJavaParser.ClassDeclarationContext;
import minijava.MiniJavaParser.MethodDeclarationContext;
import minijava.MiniJavaParser.OtherTypeContext;
import minijava.MiniJavaParser.VarDeclarationContext;

public class SymbolTableVisitor extends MiniJavaBaseVisitor<Entry> {
	private final Program program;

	public SymbolTableVisitor() {
		program = new Program();
		Class intClass = new Class("int");
		program.add(intClass);
		Class intArrayClass = new Class("int[]");
		program.add(intArrayClass);
		Class booleanClass = new Class("boolean");
		program.add(booleanClass);
	}
	
	protected List<Entry> visit(List<? extends ParserRuleContext> ctxs) {
		
		LinkedList<Entry> result = new LinkedList<>();
		
		for (ParserRuleContext ctx : ctxs) {
			result.add(visit(ctx));
		}
		return result;
	}

	@Override
	public Entry visitVarDeclaration(VarDeclarationContext ctx) {

		return new Variable(ctx.type().getText(), ctx.identifier().getText());
	}
	
	@Override
	public Entry visitMethodDeclaration(MethodDeclarationContext ctx) {
		Method method = new Method(ctx.methodName.getText(), ctx.returnType.getText());
		
		method.addLocalVariable((Variable[]) visit(ctx.varDeclaration()).toArray());
		method.addParameters((Method[]) visit(ctx.).toArray());
	}
	
	@Override
	public Entry visitClassDeclaration(ClassDeclarationContext ctx) {
		String className = ctx.className.IDENTIFIER().getText();
		
		if (program.contains(className)) {
			System.out.println("Class "+className+" has already been declared.");
			return null;
		} else {
			Class clazz = new Class(className);
			
			for (VarDeclarationContext varDeclarationCtx : ctx.varDeclaration()) {
				clazz.add((Variable) visit(varDeclarationCtx));
			}
			
			for (MethodDeclarationContext methodDeclarationCtx : ctx.methodDeclaration()) {
				clazz.add((Method) visit(methodDeclarationCtx));
			}
			
			program.add(clazz);
			return clazz;
		}
		
	}
}
