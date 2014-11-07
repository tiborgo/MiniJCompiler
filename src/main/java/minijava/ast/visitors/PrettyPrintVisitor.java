package minijava.ast.visitors;

import java.util.List;

import minijava.ast.rules.DeclClass;
import minijava.ast.rules.DeclMain;
import minijava.ast.rules.DeclMeth;
import minijava.ast.rules.DeclVar;
import minijava.ast.rules.Exp;
import minijava.ast.rules.ExpArrayGet;
import minijava.ast.rules.ExpArrayLength;
import minijava.ast.rules.ExpBinOp;
import minijava.ast.rules.ExpFalse;
import minijava.ast.rules.ExpId;
import minijava.ast.rules.ExpIntConst;
import minijava.ast.rules.ExpInvoke;
import minijava.ast.rules.ExpNeg;
import minijava.ast.rules.ExpNew;
import minijava.ast.rules.ExpNewIntArray;
import minijava.ast.rules.ExpThis;
import minijava.ast.rules.ExpTrue;
import minijava.ast.rules.Parameter;
import minijava.ast.rules.Prg;
import minijava.ast.rules.Stm;
import minijava.ast.rules.StmArrayAssign;
import minijava.ast.rules.StmAssign;
import minijava.ast.rules.StmIf;
import minijava.ast.rules.StmList;
import minijava.ast.rules.StmPrintChar;
import minijava.ast.rules.StmPrintlnInt;
import minijava.ast.rules.StmWhile;
import minijava.ast.rules.TyArr;
import minijava.ast.rules.TyBool;
import minijava.ast.rules.TyClass;
import minijava.ast.rules.TyInt;
import minijava.ast.rules.TyVoid;

public class PrettyPrintVisitor implements PrgVisitor<String, RuntimeException> {

	private static final String indentStep = "  ";
	private final String indent;

	public PrettyPrintVisitor(String indent) {
		this.indent = indent;
	}

	private String prettyPrintClassList(List<DeclClass> cl, String indent) {
		StringBuffer classes = new StringBuffer();
		String sep = "";
		for (DeclClass d : cl) {
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
			DeclVisitor<String, RuntimeException> {

		private final String indent;

		public PrettyPrintVisitorDecl(String indent) {
			this.indent = indent;
		}

		private String prettyPrintMethList(List<DeclMeth> dm, String indent) {
			StringBuffer meths = new StringBuffer();
			for (DeclMeth m : dm) {
				meths.append("\n");
				meths.append(m.accept(new PrettyPrintVisitorDecl(indent)));
			}
			return meths.toString();
		}

		@Override
		public String visit(DeclClass c) {
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
		public String visit(DeclMeth m) {
			String params = "", sep = "";
			for (Parameter p : m.parameters) {
				params += sep + p.ty.accept(new PrettyPrintVisitorTy()) + " "
						+ p.id;
				sep = ", ";
			}

			StringBuilder body = new StringBuilder();
			for (Stm stm : m.body) {
				String stmRepresentation = stm
						.accept(new PrettyPrintVisitorStm(indent + indentStep));
				body.append(stmRepresentation);
			}

			return indent + "public " + m.ty.accept(new PrettyPrintVisitorTy())
					+ " " + m.methodName + " (" + params + ") {\n"
					+ prettyPrintVarList(m.localVars, indent + indentStep)
					+ body.toString() + indent + indentStep + "return "
					+ m.returnExp.accept(new PrettyPrintVisitorExp()) + ";\n"
					+ indent + "}\n";
		}

		private String prettyPrintVarList(List<DeclVar> dl, String indent) {
			StringBuffer decls = new StringBuffer();
			for (DeclVar d : dl) {
				decls.append(d.accept(new PrettyPrintVisitorDecl(indent)));
				decls.append("\n");
			}
			return decls.toString();
		}

		@Override
		public String visit(DeclVar d) {
			return indent + d.ty.accept(new PrettyPrintVisitorTy()) + " "
					+ d.name.toString() + ";";
		}

		@Override
		public String visit(DeclMain d) {
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
