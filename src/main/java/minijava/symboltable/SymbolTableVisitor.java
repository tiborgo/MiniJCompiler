package minijava.symboltable;

import java.util.ArrayList;
import java.util.Collections;
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
import minijava.ast.rules.Prg;
import minijava.ast.rules.StmArrayAssign;
import minijava.ast.rules.StmAssign;
import minijava.ast.rules.StmIf;
import minijava.ast.rules.StmList;
import minijava.ast.rules.StmPrintChar;
import minijava.ast.rules.StmPrintlnInt;
import minijava.ast.rules.StmWhile;
import minijava.ast.visitors.ASTVisitor;
import minijava.ast.visitors.DeclVisitor;
import minijava.ast.visitors.ExpVisitor;
import minijava.ast.visitors.StmVisitor;
import minijava.intermediate.Label;
import minijava.intermediate.tree.TreeExp;
import minijava.intermediate.tree.TreeExpCALL;
import minijava.intermediate.tree.TreeExpCONST;
import minijava.intermediate.tree.TreeExpNAME;
import minijava.intermediate.tree.TreeExpOP;
import minijava.intermediate.tree.TreeStm;
import minijava.intermediate.tree.TreeExpOP.Op;

public class SymbolTableVisitor implements ASTVisitor<Program, RuntimeException> {

	@Override
	public Program visit(Prg p) throws RuntimeException {

		Program program = new Program();
		program.add((Class) declVisitor.visit(p.mainClass));
		for (DeclClass clazz : p.classes) {
			program.add((Class) declVisitor.visit(clazz));
		}
		  
		return program;
	}
	
	private DeclVisitor<Entry, RuntimeException> declVisitor = new DeclVisitor<Entry, RuntimeException>() {

		@Override
		public Entry visit(DeclClass c) throws RuntimeException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Entry visit(DeclMain d) throws RuntimeException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Entry visit(DeclMeth m) throws RuntimeException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Entry visit(DeclVar d) throws RuntimeException {
			// TODO Auto-generated method stub
			return null;
		}
		
	};

	static class IntermediateVisitorExp implements
			ExpVisitor<TreeExp, RuntimeException> {

		@Override
		public TreeExp visit(ExpTrue e) throws RuntimeException {
			return new TreeExpCONST(1);
		}

		@Override
		public TreeExp visit(ExpFalse e) throws RuntimeException {
			return new TreeExpCONST(0);
		}

		@Override
		public TreeExp visit(ExpThis e) throws RuntimeException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public TreeExp visit(ExpNewIntArray e) throws RuntimeException {
			TreeExp arraySize = e.size.accept(this);
			return new TreeExpCALL(new TreeExpNAME(new Label("L_halloc")),
					Collections.singletonList(arraySize));
		}

		@Override
		public TreeExp visit(ExpNew e) throws RuntimeException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public TreeExp visit(ExpNeg e) throws RuntimeException {
			TreeExp negatedExpression = e.body.accept(this);
			if (!(negatedExpression instanceof TreeExpCONST)) {
				throw new IllegalArgumentException(
						"Unable to negate the expression \"" + e.prettyPrint()
								+ "\"");
			}
			TreeExpCONST negatedBoolean = (TreeExpCONST) negatedExpression;
			assert (negatedBoolean.value == 0 || negatedBoolean.value == 1);
			return new TreeExpCONST(1 - negatedBoolean.value);
		}

		@Override
		public TreeExp visit(ExpBinOp e) throws RuntimeException {
			Op operator = null;
			switch (e.op) {
			case PLUS:
				operator = Op.PLUS;
				break;
			case MINUS:
				operator = Op.MINUS;
				break;
			case TIMES:
				operator = Op.MUL;
				break;
			case DIV:
				operator = Op.DIV;
				break;
			case AND:
				operator = Op.AND;
				break;
			case LT:
				// TODO
				break;
			default:
				throw new IllegalArgumentException("Unknown operator: " + e.op);
			}
			return new TreeExpOP(operator, e.left.accept(this),
					e.right.accept(this));
		}

		@Override
		public TreeExp visit(ExpArrayGet e) throws RuntimeException {
			// MEM(BinOp(+, translate(array), BinOp(*, translate(index),
			// BinOp(+, WORD_SIZE, CONST(1))))
			/*
			 * TreeExp array = e.array.accept(this); TreeExp index =
			 * e.index.accept(this); new TreeExpMEM(addr);
			 */
			return null;
		}

		@Override
		public TreeExp visit(ExpArrayLength e) throws RuntimeException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public TreeExp visit(ExpInvoke e) throws RuntimeException {
			// TODO
			/*
			 * TreeExp object = e.obj.accept(this); if (!(object instanceof
			 * TreeExpCONST)) { throw new
			 * RuntimeException("Unable to invoke method on object \""
			 * +e.obj.prettyPrint()+"\""); }
			 */
			String className = "";
			String methodName = e.method;
			// FIXME: Retrieve function label with the respective mangled name
			TreeExp function = new TreeExpNAME(new Label(mangle(className,
					methodName)));
			List<TreeExp> arguments = new ArrayList<>(e.args.size());
			for (Exp exp : e.args) {
				TreeExp iRExpression = exp.accept(this);
				arguments.add(iRExpression);
			}
			return null;
		}

		@Override
		public TreeExp visit(ExpIntConst e) throws RuntimeException {
			return new TreeExpCONST(e.value);
		}

		@Override
		public TreeExp visit(ExpId e) throws RuntimeException {
			return new TreeExpNAME(new Label(e.id));
		}

		private String mangle(String className, String methodName) {
			return className + "$" + methodName;
		}
	}

	static class IntermediateVisitorStm implements
			StmVisitor<TreeStm, RuntimeException> {

		@Override
		public TreeStm visit(StmList s) throws RuntimeException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public TreeStm visit(StmIf s) throws RuntimeException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public TreeStm visit(StmWhile s) throws RuntimeException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public TreeStm visit(StmPrintlnInt s) throws RuntimeException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public TreeStm visit(StmPrintChar s) throws RuntimeException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public TreeStm visit(StmAssign s) throws RuntimeException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public TreeStm visit(StmArrayAssign s) throws RuntimeException {
			// TODO Auto-generated method stub
			return null;
		}

	}

}