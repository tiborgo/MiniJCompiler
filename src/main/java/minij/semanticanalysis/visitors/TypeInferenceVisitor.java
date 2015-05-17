package minij.semanticanalysis.visitors;

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

public class TypeInferenceVisitor implements ProgramVisitor<Void, RuntimeException> {

	public TypeInferenceVisitor() {
	}
	
	@Override
	public Void visit(Program program) throws RuntimeException {
		TypeInferenceVisitorExpTyStm expTyStmVisitor = new TypeInferenceVisitorExpTyStm(program);
		for (minij.parse.rules.declarations.Class clazz : program.getClasses()) {
			clazz.accept(expTyStmVisitor);
		}
		return null;
	}

	public static class TypeInferenceVisitorExpTyStm implements
			ExpressionVisitor<Void, RuntimeException>,
			StatementVisitor<Void, RuntimeException>,
			DeclarationVisitor<Void, RuntimeException> {
		
		private final Program symbolTable;
		private minij.parse.rules.declarations.Class classContext;
		private Method methodContext;
		
		public TypeInferenceVisitorExpTyStm(Program symbolTable) {
			this.symbolTable = symbolTable;
		}

		@Override
		public Void visit(minij.parse.rules.declarations.Class c) throws RuntimeException {

			classContext = c;

			for (Variable variable : c.fields) {
				variable.accept(this);
			}

			for (Method method : c.methods) {
				method.accept(this);
			}

			classContext = null;

			return null;
		}

		@Override
		public Void visit(Main d) throws RuntimeException {
			return visit((minij.parse.rules.declarations.Class) d);
		}

		@Override
		public Void visit(Method m) throws RuntimeException {

			methodContext = classContext.getMethod(m.methodName);

			m.body.accept(this);
			m.returnExpression.accept(this);

			methodContext = null;

			return null;
		}

		@Override
		public Void visit(Variable d) throws RuntimeException {
			return null;
		}

		@Override
		public Void visit(True e) throws RuntimeException {
			e.type = new Boolean();
			return null;
		}

		@Override
		public Void visit(False e) throws RuntimeException {
			e.type = new Boolean();
			return null;
		}

		@Override
		public Void visit(This e) throws RuntimeException {
			e.type = new Class(classContext.className);
			return null;
		}

		@Override
		public Void visit(NewIntArray e) throws RuntimeException {
			e.size.accept(this);
			e.type = new Array(new Integer());
			return null;
		}

		@Override
		public Void visit(New e) throws RuntimeException {
			e.type = new Class(e.className);
			return null;
		}

		@Override
		public Void visit(Negate e) throws RuntimeException {
			e.body.accept(this);
			e.type = new Boolean();
			return null;
		}

		@Override
		public Void visit(BinOp e) throws RuntimeException {
			e.left.accept(this);
			e.right.accept(this);
			switch(e.op) {
			case PLUS:
			case MINUS:
			case TIMES:
			case DIV:
					e.type = new Integer();
				break;
			case LT:
				e.type = new Boolean();
				break;

			case AND:
				e.type = new Boolean();
				break;
			default:
				// TODO: Complain about unknown operator type
				return null;
			}
			return null;
		}

		@Override
		public Void visit(ArrayGet e) throws RuntimeException {
			e.index.accept(this);
			e.array.accept(this);
			e.type = (e.array.type != null) ? ((Array) e.array.type).type : null;
			return null;
		}

		@Override
		public Void visit(ArrayLength e) throws RuntimeException {
			e.array.accept(this);
			e.type = new Integer();
			return null;
		}

		@Override
		public Void visit(Invoke e) throws RuntimeException {
			e.obj.accept(this);
			
			// Invocation type
			Class classType = (Class) e.obj.type;
			minij.parse.rules.declarations.Class clazz = symbolTable.get(classType.c);
			Method method = clazz.getMethod(e.method);
			e.type = (method != null) ? method.type : null;

			// Argument types
			for (Expression arg : e.args) {
				arg.accept(this);
			}
			return null;
		}

		@Override
		public Void visit(IntConstant e) throws RuntimeException {
			e.type = new Integer();
			return null;
		}

		@Override
		public Void visit(Id e) throws RuntimeException {
			Variable object = methodContext.get(e.id);
			if (object == null) {
				object = classContext.getField(e.id);
			}

			if (object == null) {
				System.err.println("Unknown variable \""+e.id+"\"");
			} else {
				e.type = object.type;
			}
			return null;
		}

		@Override
		public Void visit(StatementList s) throws RuntimeException {
			for (Statement statement : s.statements) {
				statement.accept(this);
			}
			return null;
		}

		@Override
		public Void visit(If s) throws RuntimeException {
			s.cond.accept(this);
			s.bodyTrue.accept(this);
			s.bodyFalse.accept(this);
			return null;
		}

		@Override
		public Void visit(While s) throws RuntimeException {
			s.cond.accept(this);
			s.body.accept(this);
			return null;
		}

		@Override
		public Void visit(PrintlnInt s) throws RuntimeException {
			s.arg.accept(this);
			return null;
		}

		@Override
		public Void visit(PrintChar s) throws RuntimeException {
			s.arg.accept(this);
			return null;
		}

		@Override
		public Void visit(Assignment s) throws RuntimeException {
			s.id.accept(this);
			s.rhs.accept(this);
			return null;	
		}

		@Override
		public Void visit(ArrayAssignment s) throws RuntimeException {
			s.id.accept(this);
			s.rhs.accept(this);
			s.index.accept(this);
			return null;	
		}
	}
}
