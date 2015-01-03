package minijava.ast.visitors;

import java.util.List;

import minijava.ast.rules.declarations.Class;
import minijava.ast.rules.declarations.Main;
import minijava.ast.rules.declarations.Method;
import minijava.ast.rules.declarations.Variable;
import minijava.ast.rules.declarations.DeclarationVisitor;
import minijava.ast.rules.Parameter;
import minijava.ast.rules.Prg;
import minijava.ast.rules.PrgVisitor;
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
import minijava.ast.rules.expressions.ExpVisitor;
import minijava.ast.rules.statements.Stm;
import minijava.ast.rules.statements.StmArrayAssign;
import minijava.ast.rules.statements.StmAssign;
import minijava.ast.rules.statements.StmIf;
import minijava.ast.rules.statements.StmList;
import minijava.ast.rules.statements.StmPrintChar;
import minijava.ast.rules.statements.StmPrintlnInt;
import minijava.ast.rules.statements.StmVisitor;
import minijava.ast.rules.statements.StmWhile;
import minijava.ast.rules.types.TyArr;
import minijava.ast.rules.types.TyBool;
import minijava.ast.rules.types.TyClass;
import minijava.ast.rules.types.TyInt;
import minijava.ast.rules.types.TyVisitor;
import minijava.ast.rules.types.TyVoid;

public class PrettyPrintVisitor implements PrgVisitor<String, RuntimeException> {

	private static final String indentStep = "  ";
	private final String indent;

	public PrettyPrintVisitor(String indent) {
		this.indent = indent;
	}

	private String prettyPrintClassList(List<Class> cl, String indent) {
		StringBuffer classes = new StringBuffer();
		String sep = "";
		for (Class d : cl) {
			classes.append(sep);
			classes.append(d.accept(new PrettyPrintVisitorDecl(indent)));
			sep = "\n";
		}
		return classes.toString();
	}

	@Override
	public String visit(Prg p) {
		return p.mainClass.accept(new PrettyPrintVisitorDecl(indent)) + "\n"
				+ prettyPrintClassList(p.classes, indent);
	}

	public static class PrettyPrintVisitorDecl implements
			DeclarationVisitor<String, RuntimeException> {

		private final String indent;

		public PrettyPrintVisitorDecl(String indent) {
			this.indent = indent;
		}

		private String prettyPrintMethList(List<Method> dm, String indent) {
			StringBuffer meths = new StringBuffer();
			for (Method m : dm) {
				meths.append("\n");
				meths.append(m.accept(new PrettyPrintVisitorDecl(indent)));
			}
			return meths.toString();
		}

		@Override
		public String visit(Class c) {
			return indent
					+ "class "
					+ c.className
					+ (c.superName == null ? " " : " extends " + c.superName
							+ " ") + "{\n\n"
					+ prettyPrintVarList(c.fields, indent + indentStep)
					+ prettyPrintMethList(c.methods, indent + indentStep)
					+ indent + "}\n";
		}

		@Override
		public String visit(Method m) {
			String params = "", sep = "";
			for (Parameter p : m.parameters) {
				params += sep + p.ty.accept(new PrettyPrintVisitorTy()) + " "
						+ p.id;
				sep = ", ";
			}

			return indent + "public " + m.ty.accept(new PrettyPrintVisitorTy())
					+ " " + m.methodName + " (" + params + ") {\n"
					+ prettyPrintVarList(m.localVars, indent + indentStep)
					+ m.body.accept(new PrettyPrintVisitorStm(indent + indentStep)) + indent + indentStep
		            + "return " + m.returnExp.accept(new PrettyPrintVisitorExp()) + ";\n"
					+ indent + "}\n";
		}

		private String prettyPrintVarList(List<Variable> dl, String indent) {
			StringBuffer decls = new StringBuffer();
			for (Variable d : dl) {
				decls.append(d.accept(new PrettyPrintVisitorDecl(indent)));
				decls.append("\n");
			}
			return decls.toString();
		}

		@Override
		public String visit(Variable d) {
			return indent + d.ty.accept(new PrettyPrintVisitorTy()) + " "
					+ d.name.toString() + ";";
		}

		@Override
		public String visit(Main d) {
			return indent
					+ "class "
					+ d.className.toString()
					+ " {\n"
					+ indent
					+ indentStep
					+ "public static void main (String[] "
					+ d.mainArg.toString()
					+ ") {\n"
					+ d.mainBody.accept(new PrettyPrintVisitorStm(indent
							+ indentStep + indentStep)) + indent + indentStep
					+ "}\n" + indent + "}\n";
		}

	}

	public static class PrettyPrintVisitorTy implements
			TyVisitor<String, RuntimeException> {

		@Override
		public String visit(TyVoid b) {
			return "void";
		}

		@Override
		public String visit(TyBool b) {
			return "boolean";
		}

		@Override
		public String visit(TyInt i) {
			return "int";
		}

		@Override
		public String visit(TyClass x) {
			return x.c.toString();
		}

		@Override
		public String visit(TyArr x) {
			return x.ty.accept(this) + "[]";
		}
	}

	public static class PrettyPrintVisitorExp implements
			ExpVisitor<String, RuntimeException> {

		@Override
		public String visit(ExpTrue x) {
			return "true";
		}

		@Override
		public String visit(ExpFalse x) {
			return "false";
		}

		@Override
		public String visit(ExpThis x) {
			return "this";
		}

		@Override
		public String visit(ExpNewIntArray x) {
			return "new int [" + x.size.accept(this) + "]";
		}

		@Override
		public String visit(ExpNew x) {
			return "new " + x.className.toString() + "()";
		}

		@Override
		public String visit(ExpBinOp e) {
			return "(" + e.left.accept(this) + e.op.toString()
					+ e.right.accept(this) + ")";
		}

		@Override
		public String visit(ExpArrayGet e) {
			return e.array.accept(this) + "[" + e.index.accept(this) + "]";
		}

		@Override
		public String visit(ExpArrayLength e) {
			return e.array.accept(this) + ".length";
		}

		@Override
		public String visit(ExpInvoke e) {
			String args = "";
			String sep = "";
			for (Exp ea : e.args) {
				args += sep + ea.accept(new PrettyPrintVisitorExp());
				sep = ", ";
			}

			return e.obj.accept(this) + "." + e.method + "(" + args + ")";
		}

		@Override
		public String visit(ExpIntConst x) {
			return (new Integer(x.value)).toString();
		}

		@Override
		public String visit(ExpId x) {
			return x.id;
		}

		@Override
		public String visit(ExpNeg x) {
			return "!(" + x.body.accept(this) + ")";
		}
	}

	public static class PrettyPrintVisitorStm implements
			StmVisitor<String, RuntimeException> {

		final String indent;

		public PrettyPrintVisitorStm() {
			this.indent = "";
		}

		public PrettyPrintVisitorStm(String indent) {
			this.indent = indent;
		}

		@Override
		public String visit(StmList slist) {
			StringBuffer str = new StringBuffer();
			for (Stm s : slist.stms) {
				str.append(s.accept(new PrettyPrintVisitorStm(indent)));
			}
			return str.toString();
		}

		@Override
		public String visit(StmIf s) {
			return indent
					+ "if ("
					+ s.cond.accept(new PrettyPrintVisitorExp())
					+ ") {\n"
					+ s.bodyTrue.accept(new PrettyPrintVisitorStm(this.indent
							+ indentStep))
					+ indent
					+ "} else {\n"
					+ s.bodyFalse.accept(new PrettyPrintVisitorStm(this.indent
							+ indentStep)) + indent + "}\n";
		}

		@Override
		public String visit(StmWhile s) {

			return indent
					+ "while ("
					+ s.cond.accept(new PrettyPrintVisitorExp())
					+ ") {\n"
					+ s.body.accept(new PrettyPrintVisitorStm(this.indent + " "))
					+ indent + "}\n";
		}

		@Override
		public String visit(StmPrintlnInt s) {
			return indent + "System.out.println("
					+ s.arg.accept(new PrettyPrintVisitorExp()) + ");\n";
		}

		@Override
		public String visit(StmPrintChar s) {
			return indent + "System.out.print((char)"
					+ s.arg.accept(new PrettyPrintVisitorExp()) + ");\n";
		}

		@Override
		public String visit(StmAssign s) {
			return indent + s.id + " = "
					+ s.rhs.accept(new PrettyPrintVisitorExp()) + ";\n";
		}

		@Override
		public String visit(StmArrayAssign s) {
			return indent + s.id + "["
					+ s.index.accept(new PrettyPrintVisitorExp()) + "] = "
					+ s.rhs.accept(new PrettyPrintVisitorExp()) + ";\n";
		}
	}
}
