package minijava.symboltable.visitors;

import minijava.ast.rules.declarations.Main;
import minijava.ast.rules.declarations.Method;
import minijava.ast.rules.declarations.Variable;
import minijava.ast.rules.declarations.DeclarationVisitor;
import minijava.ast.rules.Prg;
import minijava.ast.rules.PrgVisitor;
import minijava.ast.rules.expressions.ArrayGet;
import minijava.ast.rules.expressions.ArrayLength;
import minijava.ast.rules.expressions.BinOp;
import minijava.ast.rules.expressions.False;
import minijava.ast.rules.expressions.Id;
import minijava.ast.rules.expressions.IntConstant;
import minijava.ast.rules.expressions.Invoke;
import minijava.ast.rules.expressions.Negate;
import minijava.ast.rules.expressions.New;
import minijava.ast.rules.expressions.NewIntArray;
import minijava.ast.rules.expressions.This;
import minijava.ast.rules.expressions.True;
import minijava.ast.rules.expressions.ExpressionVisitor;
import minijava.ast.rules.statements.Statement;
import minijava.ast.rules.statements.ArrayAssignment;
import minijava.ast.rules.statements.Assignment;
import minijava.ast.rules.statements.If;
import minijava.ast.rules.statements.StatementList;
import minijava.ast.rules.statements.PrintChar;
import minijava.ast.rules.statements.PrintlnInt;
import minijava.ast.rules.statements.StatementVisitor;
import minijava.ast.rules.statements.While;
import minijava.ast.rules.types.Type;
import minijava.ast.rules.types.Array;
import minijava.ast.rules.types.Boolean;
import minijava.ast.rules.types.Class;
import minijava.ast.rules.types.Integer;
import minijava.ast.rules.types.TypeVisitor;
import minijava.ast.rules.types.Void;
import minijava.symboltable.tree.Program;

public class TypeCheckVisitor implements PrgVisitor<java.lang.Boolean, RuntimeException>,
		DeclarationVisitor<java.lang.Boolean, RuntimeException> {
	
	private final Program symbolTable;
	private minijava.symboltable.tree.Class classContext;
	private minijava.symboltable.tree.Method methodContext;

	public TypeCheckVisitor(Program symbolTable) {
		this.symbolTable = symbolTable;
	}
	
	@Override
	public java.lang.Boolean visit(Prg p) throws RuntimeException {

		boolean ok = true;
		ok = visit(p.mainClass) ? ok : false;
		for (minijava.ast.rules.declarations.Class clazz : p.classes) {
			ok = clazz.accept(this) ? ok : false;
		}
		return ok;
	}
	
	@Override
	public java.lang.Boolean visit(minijava.ast.rules.declarations.Class c) throws RuntimeException {
		
		classContext = symbolTable.classes.get(c.className);
		
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
		return true;
	}

	@Override
	public java.lang.Boolean visit(Method m) throws RuntimeException {
		
		methodContext = classContext.methods.get(m.methodName);
		
		boolean ok = true;
		
		TypeCheckVisitorExpTyStm typeCheckVisitor = new TypeCheckVisitorExpTyStm(symbolTable, classContext, methodContext);
		
		ok = (m.body.accept(typeCheckVisitor)) ? ok : false;
		ok = (m.returnExpression.accept(typeCheckVisitor).equals(m.type)) ? ok : false;
		
		methodContext = null;
		
		return ok;
	}

	@Override
	public java.lang.Boolean visit(Variable d) throws RuntimeException {
		return d.type.accept(new TypeCheckVisitorExpTyStm(symbolTable, classContext, methodContext));
	} 
	
	public static class TypeCheckVisitorExpTyStm implements
			ExpressionVisitor<Type, RuntimeException>,
			TypeVisitor<java.lang.Boolean, RuntimeException>,
			StatementVisitor<java.lang.Boolean, RuntimeException> {
		
		private final Program symbolTable;
		private final minijava.symboltable.tree.Class classContext;
		private final minijava.symboltable.tree.Method methodContext;
		
		public TypeCheckVisitorExpTyStm(Program symbolTable, minijava.symboltable.tree.Class classContext, minijava.symboltable.tree.Method methodContext) {
			this.symbolTable = symbolTable;
			this.classContext = classContext;
			this.methodContext = methodContext;
		}
		
		
		@Override
		public Type visit(True e) throws RuntimeException {
			return new Boolean();
		}

		@Override
		public Type visit(False e) throws RuntimeException {
			return new Boolean();
		}

		@Override
		public Type visit(This e) throws RuntimeException {
			return new Class(classContext.name);
		}

		@Override
		public Type visit(NewIntArray e) throws RuntimeException {
			
			if (e.size.accept(this) instanceof Integer) {
				return new Array(new Integer());
			}
			else {
				// TODO: error
				return null;
			}
		}

		@Override
		public Type visit(New e) throws RuntimeException {
				
			// Check if class exists
			if (symbolTable.classes.containsKey(e.className)) {
				return new Class(e.className);
			}
			else {
				// TODO: error
				return null;
			}
		}

		@Override
		public Type visit(Negate e) throws RuntimeException {
			if (e.body.accept(this) instanceof Boolean) {
				return new Boolean();
			}
			else {
				System.err.println("Neg operator can only be applied to boolean expression");
				return null;
			}
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
					return new Integer();
				}
				else {
					System.err.println("Both operands of binary operation '" + e.op + "' must have type int");
					return null;
				}

			case LT:
				if (e.left.accept(this) instanceof Integer &&
						e.right.accept(this) instanceof Integer) {
					return new Boolean();
				}
				else {
					System.err.println("Both operands of binary operation '" + e.op + "' must have type int");
					return null;
				}

			case AND:
				if (e.left.accept(this) instanceof Boolean &&
						e.right.accept(this) instanceof Boolean) {
					return new Boolean();
				}
				else {
					System.err.println("Both operands of binary operation '" + e.op + "' must have type bool");
					return null;
				}
			default:
				return null;
			}
		}

		@Override
		public Type visit(ArrayGet e) throws RuntimeException {
			
			Type indexType = e.index.accept(this);
			Type arrayType = e.array.accept(this);
			
			if (indexType instanceof Integer &&
					arrayType instanceof Array) {
				return ((Array) arrayType).type;
			}
			else {
				System.err.println("Array get error");
				return null;
			}
		}

		@Override
		public Type visit(ArrayLength e) throws RuntimeException {
			if (e.array.accept(this) instanceof Array) {
				return new Integer();
			}
			else {
				System.err.println("'length' must be applied to array type");
				return null;
			}
		}

		@Override
		public Type visit(Invoke e) throws RuntimeException {
			
			Class object = (Class) e.obj.accept(this);
			
			// Check class
			minijava.symboltable.tree.Class clazz   = symbolTable.classes.get(object.c);
			if (clazz == null) {
				// TODO: error
				return null;
			}
			
			// Check method
			minijava.symboltable.tree.Method method = new MethodVisitor(clazz, e.method).visit(symbolTable);
			if (method == null) {
				// TODO: error
				return null;
			}
			
			// Check arguments
			if (e.args.size() == method.parametersList.size()) {
				
				boolean ok = true;
				for (int i = 0; i < e.args.size(); i++) {
					Type argType = e.args.get(i).accept(this);
					if (!argType.equals(method.parametersList.get(i).type)) {
						ok = false;
						// TODO: error
					}
				}
				
				if (ok) {
					return method.returnType;
				}
				else {
					return null;
				}
			}
			else {
				// TODO: error
				return null;
			}
		}

		@Override
		public Type visit(IntConstant e) throws RuntimeException {
			return new Integer();
		}

		@Override
		public Type visit(Id e) throws RuntimeException {
			// TODO: Should be replaced with a lookup in an appropriate data structure that honours variable visibility
			minijava.symboltable.tree.Variable object = methodContext.get(e.id);
			if (object == null) {
				object = classContext.fields.get(e.id);
			}

			if (object == null) {
				// TODO: error
				System.err.println("Unknown variable \""+e.id+"\"");
				return null;
			}
			else {
				return object.type;
			}
		}
		
		public java.lang.Boolean visit(Type t) {
			return false;
		}

		@Override
		public java.lang.Boolean visit(Void t) {
			return true;
		}

		@Override
		public java.lang.Boolean visit(Boolean t) {
			return true;
		}

		@Override
		public java.lang.Boolean visit(Integer t) {
			return true;
		}

		@Override
		public java.lang.Boolean visit(Class t) {
			return symbolTable.classes.containsKey(t.c);
		}

		@Override
		public java.lang.Boolean visit(Array t) {
			return visit(t.type);
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
			Type type = s.cond.accept(this);
			if (!(type instanceof Boolean)) {
				return java.lang.Boolean.FALSE;
			}
			return s.bodyTrue.accept(this).booleanValue() && s.bodyFalse.accept(this).booleanValue();
		}

		@Override
		public java.lang.Boolean visit(While s) throws RuntimeException {
			Type type = s.cond.accept(this);
			if (!(type instanceof Boolean)) {
				return java.lang.Boolean.FALSE;
			}
			return s.body.accept(this);
		}

		@Override
		public java.lang.Boolean visit(PrintlnInt s) throws RuntimeException {
			Type expressionType = s.arg.accept(this);
			if (!(expressionType instanceof Integer)) {
				return java.lang.Boolean.FALSE;
			}
			return java.lang.Boolean.TRUE;
		}

		@Override
		public java.lang.Boolean visit(PrintChar s) throws RuntimeException {
			//Ty expressionType = s.arg.accept(this);
			// TODO: No type class for type char?
			return java.lang.Boolean.TRUE;
		}

		@Override
		public java.lang.Boolean visit(Assignment s) throws RuntimeException {
			Type idType     =  new Id(s.id).accept(this);
			Type assignType = s.rhs.accept(this);
			
			if (idType.equals(assignType)) {
				return java.lang.Boolean.TRUE;
			}
			else {
				return java.lang.Boolean.FALSE;
			}
		}

		@Override
		public java.lang.Boolean visit(ArrayAssignment s) throws RuntimeException {
			Type arrayType  = new Id(s.id).accept(this);
			Type assignType = s.rhs.accept(this);
			Type indexType  = s.index.accept(this);
			
			if (assignType.equals(arrayType) &&
					indexType.equals(new Integer())) {
				return java.lang.Boolean.TRUE;
			}
			else {
				return java.lang.Boolean.FALSE;
			}
		}
	}
}