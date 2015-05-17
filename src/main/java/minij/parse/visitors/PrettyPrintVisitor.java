package minij.parse.visitors;

import java.util.List;

import minij.parse.rules.Parameter;
import minij.parse.rules.Program;
import minij.parse.rules.ProgramVisitor;
import minij.parse.rules.declarations.DeclarationVisitor;
import minij.parse.rules.declarations.Main;
import minij.parse.rules.declarations.Method;
import minij.parse.rules.declarations.Variable;
import minij.parse.rules.expressions.ArrayGet;
import minij.parse.rules.expressions.ArrayLength;
import minij.parse.rules.expressions.BinOp;
import minij.parse.rules.expressions.Expression;
import minij.parse.rules.expressions.ExpressionVisitor;
import minij.parse.rules.expressions.False;
import minij.parse.rules.expressions.Id;
import minij.parse.rules.expressions.IntConstant;
import minij.parse.rules.expressions.Invoke;
import minij.parse.rules.expressions.Negate;
import minij.parse.rules.expressions.New;
import minij.parse.rules.expressions.NewIntArray;
import minij.parse.rules.expressions.This;
import minij.parse.rules.expressions.True;
import minij.parse.rules.statements.ArrayAssignment;
import minij.parse.rules.statements.Assignment;
import minij.parse.rules.statements.If;
import minij.parse.rules.statements.PrintChar;
import minij.parse.rules.statements.PrintlnInt;
import minij.parse.rules.statements.Statement;
import minij.parse.rules.statements.StatementList;
import minij.parse.rules.statements.StatementVisitor;
import minij.parse.rules.statements.While;
import minij.parse.rules.types.Array;
import minij.parse.rules.types.Boolean;
import minij.parse.rules.types.Class;
import minij.parse.rules.types.Integer;
import minij.parse.rules.types.TypeVisitor;
import minij.parse.rules.types.Void;

public class PrettyPrintVisitor implements ProgramVisitor<String, RuntimeException> {

	private static final String indentStep = "  ";
	private final String indent;

	public PrettyPrintVisitor(String indent) {
		this.indent = indent;
	}

	@Override
	public String visit(Program p) {
		
		StringBuffer classes = new StringBuffer();
		String sep = "";
		for (minij.parse.rules.declarations.Class d : p.getClasses()) {
			classes.append(sep);
			classes.append(d.accept(new PrettyPrintVisitorDecl(indent)));
			sep = "\n";
		}
		return classes.toString();
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
		public String visit(minij.parse.rules.declarations.Class c) {
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
				params += sep + p.type.accept(new PrettyPrintVisitorTy()) + " "
						+ p.id;
				sep = ", ";
			}

			return indent + "public " + m.type.accept(new PrettyPrintVisitorTy())
					+ " " + m.methodName + " (" + params + ") {\n"
					+ prettyPrintVarList(m.localVars, indent + indentStep)
					+ m.body.accept(new PrettyPrintVisitorStm(indent + indentStep)) + indent + indentStep
		            + "return " + m.returnExpression.accept(new PrettyPrintVisitorExp()) + ";\n"
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
			return indent + d.type.accept(new PrettyPrintVisitorTy()) + " "
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
					+ d.mainMethod.methodName
					+ ") {\n"
					+ d.mainMethod.body.accept(new PrettyPrintVisitorStm(indent
							+ indentStep + indentStep)) + indent + indentStep
					+ "}\n" + indent + "}\n";
		}

	}

	public static class PrettyPrintVisitorTy implements
			TypeVisitor<String, RuntimeException> {

		@Override
		public String visit(Void b) {
			return "void";
		}

		@Override
		public String visit(Boolean b) {
			return "boolean";
		}

		@Override
		public String visit(Integer i) {
			return "int";
		}

		@Override
		public String visit(Class x) {
			return x.c.toString();
		}

		@Override
		public String visit(Array x) {
			return x.type.accept(this) + "[]";
		}
	}

	public static class PrettyPrintVisitorExp implements
			ExpressionVisitor<String, RuntimeException> {

		@Override
		public String visit(True x) {
			return "true";
		}

		@Override
		public String visit(False x) {
			return "false";
		}

		@Override
		public String visit(This x) {
			return "this";
		}

		@Override
		public String visit(NewIntArray x) {
			return "new int [" + x.size.accept(this) + "]";
		}

		@Override
		public String visit(New x) {
			return "new " + x.className.toString() + "()";
		}

		@Override
		public String visit(BinOp e) {
			return "(" + e.left.accept(this) + e.op.toString()
					+ e.right.accept(this) + ")";
		}

		@Override
		public String visit(ArrayGet e) {
			return e.array.accept(this) + "[" + e.index.accept(this) + "]";
		}

		@Override
		public String visit(ArrayLength e) {
			return e.array.accept(this) + ".length";
		}

		@Override
		public String visit(Invoke e) {
			String args = "";
			String sep = "";
			for (Expression ea : e.args) {
				args += sep + ea.accept(new PrettyPrintVisitorExp());
				sep = ", ";
			}

			return e.obj.accept(this) + "." + e.method + "(" + args + ")";
		}

		@Override
		public String visit(IntConstant x) {
			return (new java.lang.Integer(x.value)).toString();
		}

		@Override
		public String visit(Id x) {
			return x.id;
		}

		@Override
		public String visit(Negate x) {
			return "!(" + x.body.accept(this) + ")";
		}
	}

	public static class PrettyPrintVisitorStm implements
			StatementVisitor<String, RuntimeException> {

		final String indent;

		public PrettyPrintVisitorStm() {
			this.indent = "";
		}

		public PrettyPrintVisitorStm(String indent) {
			this.indent = indent;
		}

		@Override
		public String visit(StatementList slist) {
			StringBuffer str = new StringBuffer();
			for (Statement s : slist.statements) {
				str.append(s.accept(new PrettyPrintVisitorStm(indent)));
			}
			return str.toString();
		}

		@Override
		public String visit(If s) {
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
		public String visit(While s) {

			return indent
					+ "while ("
					+ s.cond.accept(new PrettyPrintVisitorExp())
					+ ") {\n"
					+ s.body.accept(new PrettyPrintVisitorStm(this.indent + " "))
					+ indent + "}\n";
		}

		@Override
		public String visit(PrintlnInt s) {
			return indent + "System.out.println("
					+ s.arg.accept(new PrettyPrintVisitorExp()) + ");\n";
		}

		@Override
		public String visit(PrintChar s) {
			return indent + "System.out.print((char)"
					+ s.arg.accept(new PrettyPrintVisitorExp()) + ");\n";
		}

		@Override
		public String visit(Assignment s) {
			return indent + s.id.id + " = "
					+ s.rhs.accept(new PrettyPrintVisitorExp()) + ";\n";
		}

		@Override
		public String visit(ArrayAssignment s) {
			return indent + s.id.id + "["
					+ s.index.accept(new PrettyPrintVisitorExp()) + "] = "
					+ s.rhs.accept(new PrettyPrintVisitorExp()) + ";\n";
		}
	}
}
