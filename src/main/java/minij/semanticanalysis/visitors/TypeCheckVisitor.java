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
package minij.semanticanalysis.visitors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import minij.parse.rules.Program;
import minij.parse.rules.ProgramVisitor;
import minij.parse.rules.declarations.DeclarationVisitor;
import minij.parse.rules.declarations.Main;
import minij.parse.rules.declarations.Method;
import minij.parse.rules.declarations.Variable;
import minij.parse.rules.expressions.ArrayGet;
import minij.parse.rules.expressions.ArrayLength;
import minij.parse.rules.expressions.BinOp;
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
import minij.parse.rules.types.Type;
import minij.parse.rules.types.TypeVisitor;
import minij.parse.rules.types.Void;
import minij.parse.visitors.PrettyPrintVisitor;

public class TypeCheckVisitor implements ProgramVisitor<java.lang.Boolean, RuntimeException> {
	private TypeCheckVisitorExpTyStm expTyStmVisitor;
	
	public TypeCheckVisitor() {
	}
	
	@Override
	public java.lang.Boolean visit(Program program) throws RuntimeException {
		expTyStmVisitor = new TypeCheckVisitorExpTyStm(program);

		boolean ok = true;
		for (minij.parse.rules.declarations.Class clazz : program.getClasses()) {
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
		private minij.parse.rules.declarations.Class classContext;
		private Method methodContext;

		public TypeCheckVisitorExpTyStm(Program symbolTable) {
			this.symbolTable = symbolTable;
			errors = new ArrayList<>();
			expressionPrettyPrinter = new PrettyPrintVisitor.PrettyPrintVisitorExp();
		}

		@Override
		public java.lang.Boolean visit(minij.parse.rules.declarations.Class c) throws RuntimeException {

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
			return visit((minij.parse.rules.declarations.Class) d);
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
				return e.type;
			}
			else {
				return null;
			}
		}

		@Override
		public Type visit(New e) throws RuntimeException {
			// Check if class exists
			if (symbolTable.contains(e.className)) {
				e.type = new Class(e.className);
				return e.type;
			}
			else {
				return null;
			}
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
			minij.parse.rules.declarations.Class clazz = symbolTable.get(object.c);
			if (clazz == null) {
				return null;
			}
			
			// Check method
			Method method = clazz.getMethod(e.method);
			if (method == null) {
				return null;
			}
			
			// Check arguments
			if (e.args.size() == method.parameters.size()) {
				
				boolean ok = true;
				for (int i = 0; i < e.args.size(); i++) {
					Type argType = e.args.get(i).accept(this);
					if (!argType.equals(method.parameters.get(i).type)) {
						ok = false;
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
			Variable object = methodContext.get(e.id);
			if (object == null) {
				object = classContext.getField(e.id);
			}

			if (object == null) {
				System.err.println("Unknown variable \""+e.id+"\"");
				return null;
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
			
			Type condType = s.cond.accept(this);
			
			if (!(condType instanceof Boolean)) {
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
