package minijava.semanticanalysis.visitors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import minijava.parse.rules.Program;
import minijava.parse.rules.ProgramVisitor;
import minijava.parse.rules.declarations.DeclarationVisitor;
import minijava.parse.rules.declarations.Main;
import minijava.parse.rules.declarations.Method;
import minijava.parse.rules.declarations.Variable;
import minijava.parse.rules.expressions.ArrayGet;
import minijava.parse.rules.expressions.ArrayLength;
import minijava.parse.rules.expressions.BinOp;
import minijava.parse.rules.expressions.ExpressionVisitor;
import minijava.parse.rules.expressions.False;
import minijava.parse.rules.expressions.Id;
import minijava.parse.rules.expressions.IntConstant;
import minijava.parse.rules.expressions.Invoke;
import minijava.parse.rules.expressions.Negate;
import minijava.parse.rules.expressions.New;
import minijava.parse.rules.expressions.NewIntArray;
import minijava.parse.rules.expressions.This;
import minijava.parse.rules.expressions.True;
import minijava.parse.rules.statements.ArrayAssignment;
import minijava.parse.rules.statements.Assignment;
import minijava.parse.rules.statements.If;
import minijava.parse.rules.statements.PrintChar;
import minijava.parse.rules.statements.PrintlnInt;
import minijava.parse.rules.statements.Statement;
import minijava.parse.rules.statements.StatementList;
import minijava.parse.rules.statements.StatementVisitor;
import minijava.parse.rules.statements.While;
import minijava.parse.rules.types.Array;
import minijava.parse.rules.types.Boolean;
import minijava.parse.rules.types.Class;
import minijava.parse.rules.types.Integer;
import minijava.parse.rules.types.Type;
import minijava.parse.rules.types.TypeVisitor;
import minijava.parse.rules.types.Void;
import minijava.parse.visitors.PrettyPrintVisitor;

public class TypeCheckVisitor implements ProgramVisitor<java.lang.Boolean, RuntimeException> {
	private TypeCheckVisitorExpTyStm expTyStmVisitor;
	
	public TypeCheckVisitor() {
	}
	
	@Override
	public java.lang.Boolean visit(Program program) throws RuntimeException {
		expTyStmVisitor = new TypeCheckVisitorExpTyStm(program);

		boolean ok = true;
		for (minijava.parse.rules.declarations.Class clazz : program.getClasses()) {
			ok = clazz.accept(expTyStmVisitor) ? ok : false;
		}
		return ok;
	}

	public List<String> getErrors() {
		if (expTyStmVisitor != null) {
			return expTyStmVisitor.getErrors();
		}
		return Collections.emptyList();
	}

	public static class TypeCheckVisitorExpTyStm implements
			ExpressionVisitor<Type, RuntimeException>,
			TypeVisitor<java.lang.Boolean, RuntimeException>,
			StatementVisitor<java.lang.Boolean, RuntimeException>,
			DeclarationVisitor<java.lang.Boolean, RuntimeException> {
		
		private final Program symbolTable;
		private final List<String> errors;
		private final PrettyPrintVisitor.PrettyPrintVisitorExp expressionPrettyPrinter;
		private minijava.parse.rules.declarations.Class classContext;
		private Method methodContext;

		public TypeCheckVisitorExpTyStm(Program symbolTable) {
			this.symbolTable = symbolTable;
			errors = new ArrayList<>();
			expressionPrettyPrinter = new PrettyPrintVisitor.PrettyPrintVisitorExp();
		}

		@Override
		public java.lang.Boolean visit(minijava.parse.rules.declarations.Class c) throws RuntimeException {

			classContext = c;

			boolean ok = true;
			for (Variable variable : c.fields) {
				ok = variable.accept(this) ? ok : false;
			}

			for (Method method : c.methods) {
				ok = method.accept(this) ? ok : false;
			}

			classContext = null;

			return ok;
		}

		@Override
		public java.lang.Boolean visit(Main d) throws RuntimeException {
			return visit((minijava.parse.rules.declarations.Class) d);
		}

		@Override
		public java.lang.Boolean visit(Method m) throws RuntimeException {

			methodContext = classContext.getMethod(m.methodName);

			boolean ok = true;


			ok = (m.body.accept(this)) ? ok : false;
			ok = (m.returnExpression.accept(this) != null) ? ok : false;
			ok = (m.returnExpression.type.equals(m.type)) ? ok : false;

			methodContext = null;

			return ok;
		}

		@Override
		public java.lang.Boolean visit(Variable d) throws RuntimeException {
			return d.type.accept(this);
		}

		@Override
		public Type visit(True e) throws RuntimeException {
			e.type = new Boolean();
			return e.type;
		}

		@Override
		public Type visit(False e) throws RuntimeException {
			e.type = new Boolean();
			return e.type;
		}

		@Override
		public Type visit(This e) throws RuntimeException {
			e.type = new Class(classContext.className);
			return e.type;
		}

		@Override
		public Type visit(NewIntArray e) throws RuntimeException {
			if (e.size.accept(this) instanceof Integer) {
				e.type = new Array(new Integer());
			}
			else {
				// TODO: error
			}
			return e.type;
		}

		@Override
		public Type visit(New e) throws RuntimeException {
			// Check if class exists
			if (symbolTable.contains(e.className)) {
				e.type = new Class(e.className);
			}
			else {
				// TODO: error
			}
			return e.type;
		}

		@Override
		public Type visit(Negate e) throws RuntimeException {
			if (e.body.accept(this) instanceof Boolean) {
				e.type = new Boolean();
			}
			else {
				System.err.println("Neg operator can only be applied to boolean expression");
			}
			return e.type;
		}

		@Override
		public Type visit(BinOp e) throws RuntimeException {
			switch(e.op) {
			case PLUS:
			case MINUS:
			case TIMES:
			case DIV:
				if (e.left.accept(this) instanceof Integer &&
						e.right.accept(this) instanceof Integer) {
					e.type = new Integer();
				}
				else {
					System.err.println("Both operands of binary operation '" + e.op + "' must have type int");
					// TODO: proper exception
					return null;
				}
				break;
			case LT:
				if (e.left.accept(this) instanceof Integer &&
						e.right.accept(this) instanceof Integer) {
					e.type = new Boolean();
				}
				else {
					System.err.println("Both operands of binary operation '" + e.op + "' must have type int");
					// TODO: proper exception
					return null;
				}
				break;

			case AND:
				if (e.left.accept(this) instanceof Boolean &&
						e.right.accept(this) instanceof Boolean) {
					e.type = new Boolean();
				}
				else {
					System.err.println("Both operands of binary operation '" + e.op + "' must have type bool");
				}
				break;
			default:
				// TODO: Complain about unknown operator type
				return null;
			}
			return e.type;
		}

		@Override
		public Type visit(ArrayGet e) throws RuntimeException {
			Type indexType = e.index.accept(this);
			Type arrayType = e.array.accept(this);
			
			if (indexType instanceof Integer &&
					arrayType instanceof Array) {
				e.type = ((Array) arrayType).type;
			}
			else {
				System.err.println("Array get error");
			}
			return e.type;
		}

		@Override
		public Type visit(ArrayLength e) throws RuntimeException {
			if (e.array.accept(this) instanceof Array) {
				e.type = new Integer();
			}
			else {
				System.err.println("'length' must be applied to array type");
			}
			return e.type;
		}

		@Override
		public Type visit(Invoke e) throws RuntimeException {
			Class object = (Class) e.obj.accept(this);
			
			// Check class
			minijava.parse.rules.declarations.Class clazz = symbolTable.get(object.c);
			if (clazz == null) {
				// TODO: error
				return null;
			}
			
			// Check method
			Method method = clazz.getMethod(e.method);
			if (method == null) {
				// TODO: error
				return null;
			}
			
			// Check arguments
			if (e.args.size() == method.parameters.size()) {
				
				boolean ok = true;
				for (int i = 0; i < e.args.size(); i++) {
					Type argType = e.args.get(i).accept(this);
					if (!argType.equals(method.parameters.get(i).type)) {
						ok = false;
						// TODO: error
					}
				}
				
				if (ok) {
					e.type = method.type;
				}
				else {
					return null;
				}
			}
			else {
				// TODO: error
				return null;
			}
			return e.type;
		}

		@Override
		public Type visit(IntConstant e) throws RuntimeException {
			e.type = new Integer();
			return e.type;
		}

		@Override
		public Type visit(Id e) throws RuntimeException {
			// TODO: Should be replaced with a lookup in an appropriate data structure that honours variable visibility
			Variable object = methodContext.get(e.id);
			if (object == null) {
				object = classContext.getField(e.id);
			}

			if (object == null) {
				// TODO: error
				System.err.println("Unknown variable \""+e.id+"\"");
			} else {
				e.type = object.type;
			}
			return e.type;
		}
		
		@Override
		public java.lang.Boolean visit(Void t) {
			return java.lang.Boolean.TRUE;
		}

		@Override
		public java.lang.Boolean visit(Boolean t) {
			return java.lang.Boolean.TRUE;
		}

		@Override
		public java.lang.Boolean visit(Integer t) {
			return java.lang.Boolean.TRUE;
		}

		@Override
		public java.lang.Boolean visit(Class t) {
			return symbolTable.contains(t.c);
		}

		@Override
		public java.lang.Boolean visit(Array t) {
			return t.type.accept(this);
		}
		
		@Override
		public java.lang.Boolean visit(StatementList s) throws RuntimeException {
			for (Statement statement : s.statements) {
				if (!statement.accept(this)) {
					return java.lang.Boolean.FALSE;
				}
			}
			return java.lang.Boolean.TRUE;
		}

		@Override
		public java.lang.Boolean visit(If s) throws RuntimeException {
			if (!(s.cond.type instanceof Boolean)) {
				errors.add("Condition in if-statement \"" + s.cond.accept(expressionPrettyPrinter)
						+ "\" is no boolean expression.");
				return java.lang.Boolean.FALSE;
			}
			return s.bodyTrue.accept(this).booleanValue() && s.bodyFalse.accept(this).booleanValue();
		}

		@Override
		public java.lang.Boolean visit(While s) throws RuntimeException {
			if (!(s.cond.type instanceof Boolean)) {
				errors.add("Condition in while-statement \"" + s.cond.accept(expressionPrettyPrinter)
						+ "\" is no boolean expression.");
				return java.lang.Boolean.FALSE;
			}
			return s.body.accept(this);
		}

		@Override
		public java.lang.Boolean visit(PrintlnInt s) throws RuntimeException {
			
			if (s.arg.accept(this) == null) {
				return java.lang.Boolean.FALSE;
			}
			
			if (s.arg.type instanceof Integer) {
				return java.lang.Boolean.TRUE;
			}
			errors.add("Argument \"" + s.arg.accept(expressionPrettyPrinter)
					+ "\" in println-statement is not of type integer.");
			return java.lang.Boolean.FALSE;
		}

		@Override
		public java.lang.Boolean visit(PrintChar s) throws RuntimeException {
			// TODO: No type class for type char?
			if (s.arg.type instanceof Integer) {
				return java.lang.Boolean.TRUE;
			}
			errors.add("Argument \"" + s.arg.accept(expressionPrettyPrinter)
					+ "\" in print-statement is not of type character.");
			return java.lang.Boolean.FALSE;
		}

		@Override
		public java.lang.Boolean visit(Assignment s) throws RuntimeException {
			
			if (s.rhs.accept(this) == null) {
				return java.lang.Boolean.FALSE;
			}
			
			Type idType     =  s.id.type;
			Type assignType = s.rhs.type;
			
			if (idType.equals(assignType)) {
				return java.lang.Boolean.TRUE;
			}
			errors.add("Assignment to variable \"" + s.id.id + "\" of type \"" + idType
					+ "\" has the invalid type \"" + assignType + "\".");
			return java.lang.Boolean.FALSE;
		}

		@Override
		public java.lang.Boolean visit(ArrayAssignment s) throws RuntimeException {
			Type arrayType  = s.id.type;
			if (!(arrayType instanceof Array)) {
				errors.add("Type \"" + arrayType + "\" of expression \"" + s.id.accept(expressionPrettyPrinter)
						+ "\" is no array type.");
				return java.lang.Boolean.FALSE;
			}
			Type assignType = s.rhs.type;
			Type indexType  = s.index.type;
			
			if (!assignType.equals(((Array) arrayType).type)) {
				errors.add("Assignment to array \"" + s.id.id + "\" of type \"" + arrayType
						+ "\" has the invalid type \"" + assignType + "\".");
				return java.lang.Boolean.FALSE;
			}
			if (!(indexType instanceof Integer)) {
				errors.add("Index of array \"" + s.id.id + "\" is not of type integer.");
				return java.lang.Boolean.FALSE;
			}
			return java.lang.Boolean.TRUE;
		}

		public List<String> getErrors() {
			return Collections.unmodifiableList(errors);
		}
	}
}