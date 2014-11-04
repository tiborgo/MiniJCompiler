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
import minijava.ast.rules.Ty;
import minijava.ast.rules.TyArr;
import minijava.ast.rules.TyBool;
import minijava.ast.rules.TyClass;
import minijava.ast.rules.TyInt;
import minijava.ast.rules.TyVoid;
import minijava.symboltable.tree.Method;
import minijava.symboltable.tree.Program;
import minijava.symboltable.tree.Class;
import minijava.symboltable.tree.Variable;
import minijava.symboltable.visitors.MethodVisitor;

public class TypeCheckVisitor implements PrgVisitor<Boolean, RuntimeException>,
		DeclVisitor<Boolean, RuntimeException>,
		ExpVisitor<Ty, RuntimeException>,
		TyVisitor<Boolean> {
	
	private final Program symbolTable;
	private Class classContext;
	private Method methodContext;

	public TypeCheckVisitor(Program symbolTable) {
		this.symbolTable = symbolTable;
	}
	
	@Override
	public Boolean visit(Prg p) throws RuntimeException {

		boolean ok = true;
		ok = visit(p.mainClass) ? ok : false;
		for (DeclClass clazz : p.classes) {
			ok = visit(clazz) ? ok : false;
		}
		return ok;
	}

	@Override
	public Boolean visit(DeclClass c) throws RuntimeException {
		
		classContext = symbolTable.classes.get(c.className);
		
		boolean ok = true;
		for (DeclVar variable : c.fields) {
			ok = visit(variable) ? ok : false;
		}
		
		for (DeclMeth method : c.methods) {
			ok = visit(method) ? ok : false;
		}
		
		classContext = null;
		
		return ok;
	}

	@Override
	public Boolean visit(DeclMain d) throws RuntimeException {
		return true;
	}

	@Override
	public Boolean visit(DeclMeth m) throws RuntimeException {
		
		methodContext = classContext.methods.get(m.methodName);
		
		boolean ok = true;
		for (Stm stm : m.body) {
			ok = visit(stm) ? ok : false;
		}
		
		ok = (visit(m.returnExp).equals(m.ty)) ? ok : false;
		
		methodContext = null;
		
		return ok;
	}

	@Override
	public Boolean visit(DeclVar d) throws RuntimeException {
		return visit(d.ty);
	}

	
	public Ty visit(Exp e) throws RuntimeException {
		return null;
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
		
		if (visit(e.size) instanceof TyInt) {
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
		if (visit(e.body) instanceof TyBool) {
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
			if (visit(e.left) instanceof TyInt &&
					visit(e.right) instanceof TyInt) {
				return new TyInt();
			}
			else {
				System.err.println("Both operands of binary operation '" + e.op + "' must have type int");
				return null;
			}
			
		case AND:
		case LT:
			if (visit(e.left) instanceof TyBool &&
					visit(e.right) instanceof TyBool) {
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
		
		Ty indexType = visit(e.index);
		Ty arrayType = visit(e.array);
		
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
		if (visit(e.array) instanceof TyArr) {
			return new TyInt();
		}
		else {
			System.err.println("'length' must be applied to array type");
			return null;
		}
	}

	@Override
	public Ty visit(ExpInvoke e) throws RuntimeException {
		
		TyClass object = (TyClass) visit(e.obj);
		
		// Check class
		Class clazz   = symbolTable.classes.get(object.c);
		if (clazz == null) {
			// TODO: error
			return null;
		}
		
		// Check method
		Method method = new MethodVisitor(clazz, e.method).visit(symbolTable);
		if (method == null) {
			// TODO: error
			return null;
		}
		
		// Check arguments
		if (e.args.size() == method.parametersList.size()) {
			
			boolean ok = true;
			for (int i = 0; i < e.args.size(); i++) {
				Ty argType = visit(e.args.get(i));
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
		Variable object = methodContext.localVariables.get(e.id);
		
		if (object == null) {
			// TODO: error
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

}
