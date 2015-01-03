package minijava.symboltable.visitors;

import minijava.ast.rules.declarations.Class;
import minijava.ast.rules.declarations.Main;
import minijava.ast.rules.declarations.Method;
import minijava.ast.rules.declarations.Variable;
import minijava.ast.rules.declarations.DeclarationVisitor;
import minijava.ast.rules.Prg;
import minijava.ast.rules.PrgVisitor;
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
import minijava.ast.rules.types.Ty;
import minijava.ast.rules.types.TyArr;
import minijava.ast.rules.types.TyBool;
import minijava.ast.rules.types.TyClass;
import minijava.ast.rules.types.TyInt;
import minijava.ast.rules.types.TyVisitor;
import minijava.ast.rules.types.TyVoid;
import minijava.symboltable.tree.Program;

public class TypeCheckVisitor implements PrgVisitor<Boolean, RuntimeException>,
		DeclarationVisitor<Boolean, RuntimeException> {
	
	private final Program symbolTable;
	private minijava.symboltable.tree.Class classContext;
	private minijava.symboltable.tree.Method methodContext;

	public TypeCheckVisitor(Program symbolTable) {
		this.symbolTable = symbolTable;
	}
	
	@Override
	public Boolean visit(Prg p) throws RuntimeException {

		boolean ok = true;
		ok = visit(p.mainClass) ? ok : false;
		for (Class clazz : p.classes) {
			ok = clazz.accept(this) ? ok : false;
		}
		return ok;
	}
	
	@Override
	public Boolean visit(Class c) throws RuntimeException {
		
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
	public Boolean visit(Main d) throws RuntimeException {
		return true;
	}

	@Override
	public Boolean visit(Method m) throws RuntimeException {
		
		methodContext = classContext.methods.get(m.methodName);
		
		boolean ok = true;
		
		TypeCheckVisitorExpTyStm typeCheckVisitor = new TypeCheckVisitorExpTyStm(symbolTable, classContext, methodContext);
		
		ok = (m.body.accept(typeCheckVisitor)) ? ok : false;
		ok = (m.returnExp.accept(typeCheckVisitor).equals(m.ty)) ? ok : false;
		
		methodContext = null;
		
		return ok;
	}

	@Override
	public Boolean visit(Variable d) throws RuntimeException {
		return d.ty.accept(new TypeCheckVisitorExpTyStm(symbolTable, classContext, methodContext));
	} 
	
	public static class TypeCheckVisitorExpTyStm implements
			ExpVisitor<Ty, RuntimeException>,
			TyVisitor<Boolean, RuntimeException>,
			StmVisitor<Boolean, RuntimeException> {
		
		private final Program symbolTable;
		private final minijava.symboltable.tree.Class classContext;
		private final minijava.symboltable.tree.Method methodContext;
		
		public TypeCheckVisitorExpTyStm(Program symbolTable, minijava.symboltable.tree.Class classContext, minijava.symboltable.tree.Method methodContext) {
			this.symbolTable = symbolTable;
			this.classContext = classContext;
			this.methodContext = methodContext;
		}
		
		
		@Override
		public Ty visit(ExpTrue e) throws RuntimeException {
			return new TyBool();
		}

		@Override
		public Ty visit(ExpFalse e) throws RuntimeException {
			return new TyBool();
		}

		@Override
		public Ty visit(ExpThis e) throws RuntimeException {
			return new TyClass(classContext.name);
		}

		@Override
		public Ty visit(ExpNewIntArray e) throws RuntimeException {
			
			if (e.size.accept(this) instanceof TyInt) {
				return new TyArr(new TyInt());
			}
			else {
				// TODO: error
				return null;
			}
		}

		@Override
		public Ty visit(ExpNew e) throws RuntimeException {
				
			// Check if class exists
			if (symbolTable.classes.containsKey(e.className)) {
				return new TyClass(e.className);
			}
			else {
				// TODO: error
				return null;
			}
		}

		@Override
		public Ty visit(ExpNeg e) throws RuntimeException {
			if (e.body.accept(this) instanceof TyBool) {
				return new TyBool();
			}
			else {
				System.err.println("Neg operator can only be applied to boolean expression");
				return null;
			}
		}

		@Override
		public Ty visit(ExpBinOp e) throws RuntimeException {
			
			switch(e.op) {
			case PLUS:
			case MINUS:
			case TIMES:
			case DIV:
				if (e.left.accept(this) instanceof TyInt &&
						e.right.accept(this) instanceof TyInt) {
					return new TyInt();
				}
				else {
					System.err.println("Both operands of binary operation '" + e.op + "' must have type int");
					return null;
				}

			case LT:
				if (e.left.accept(this) instanceof TyInt &&
						e.right.accept(this) instanceof TyInt) {
					return new TyBool();
				}
				else {
					System.err.println("Both operands of binary operation '" + e.op + "' must have type int");
					return null;
				}

			case AND:
				if (e.left.accept(this) instanceof TyBool &&
						e.right.accept(this) instanceof TyBool) {
					return new TyBool();
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
		public Ty visit(ExpArrayGet e) throws RuntimeException {
			
			Ty indexType = e.index.accept(this);
			Ty arrayType = e.array.accept(this);
			
			if (indexType instanceof TyInt &&
					arrayType instanceof TyArr) {
				return ((TyArr) arrayType).ty;
			}
			else {
				System.err.println("Array get error");
				return null;
			}
		}

		@Override
		public Ty visit(ExpArrayLength e) throws RuntimeException {
			if (e.array.accept(this) instanceof TyArr) {
				return new TyInt();
			}
			else {
				System.err.println("'length' must be applied to array type");
				return null;
			}
		}

		@Override
		public Ty visit(ExpInvoke e) throws RuntimeException {
			
			TyClass object = (TyClass) e.obj.accept(this);
			
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
					Ty argType = e.args.get(i).accept(this);
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
		public Ty visit(ExpIntConst e) throws RuntimeException {
			return new TyInt();
		}

		@Override
		public Ty visit(ExpId e) throws RuntimeException {
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
		
		public Boolean visit(Ty t) {
			return false;
		}

		@Override
		public Boolean visit(TyVoid t) {
			return true;
		}

		@Override
		public Boolean visit(TyBool t) {
			return true;
		}

		@Override
		public Boolean visit(TyInt t) {
			return true;
		}

		@Override
		public Boolean visit(TyClass t) {
			return symbolTable.classes.containsKey(t.c);
		}

		@Override
		public Boolean visit(TyArr t) {
			return visit(t.ty);
		}
		
		@Override
		public Boolean visit(StmList s) throws RuntimeException {
			for (Stm statement : s.stms) {
				if (!statement.accept(this)) {
					return Boolean.FALSE;
				}
			}
			return Boolean.TRUE;
		}

		@Override
		public Boolean visit(StmIf s) throws RuntimeException {
			Ty type = s.cond.accept(this);
			if (!(type instanceof TyBool)) {
				return Boolean.FALSE;
			}
			return s.bodyTrue.accept(this).booleanValue() && s.bodyFalse.accept(this).booleanValue();
		}

		@Override
		public Boolean visit(StmWhile s) throws RuntimeException {
			Ty type = s.cond.accept(this);
			if (!(type instanceof TyBool)) {
				return Boolean.FALSE;
			}
			return s.body.accept(this);
		}

		@Override
		public Boolean visit(StmPrintlnInt s) throws RuntimeException {
			Ty expressionType = s.arg.accept(this);
			if (!(expressionType instanceof TyInt)) {
				return Boolean.FALSE;
			}
			return Boolean.TRUE;
		}

		@Override
		public Boolean visit(StmPrintChar s) throws RuntimeException {
			//Ty expressionType = s.arg.accept(this);
			// TODO: No type class for type char?
			return Boolean.TRUE;
		}

		@Override
		public Boolean visit(StmAssign s) throws RuntimeException {
			Ty idType     =  new ExpId(s.id).accept(this);
			Ty assignType = s.rhs.accept(this);
			
			if (idType.equals(assignType)) {
				return Boolean.TRUE;
			}
			else {
				return Boolean.FALSE;
			}
		}

		@Override
		public Boolean visit(StmArrayAssign s) throws RuntimeException {
			Ty arrayType  = new ExpId(s.id).accept(this);
			Ty assignType = s.rhs.accept(this);
			Ty indexType  = s.index.accept(this);
			
			if (assignType.equals(arrayType) &&
					indexType.equals(new TyInt())) {
				return Boolean.TRUE;
			}
			else {
				return Boolean.FALSE;
			}
		}
	}
}
