package minijava.intermediate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import minijava.intermediate.tree.TreeExp;
import minijava.intermediate.tree.TreeExpCALL;
import minijava.intermediate.tree.TreeExpCONST;
import minijava.intermediate.tree.TreeExpMEM;
import minijava.intermediate.tree.TreeExpNAME;
import minijava.intermediate.tree.TreeExpOP;
import minijava.intermediate.tree.TreeExpOP.Op;
import minijava.intermediate.tree.TreeStm;
import minijava.syntax.ast.DeclClass;
import minijava.syntax.ast.DeclMain;
import minijava.syntax.ast.DeclMeth;
import minijava.syntax.ast.DeclVar;
import minijava.syntax.ast.Exp;
import minijava.syntax.ast.ExpArrayGet;
import minijava.syntax.ast.ExpArrayLength;
import minijava.syntax.ast.ExpBinOp;
import minijava.syntax.ast.ExpFalse;
import minijava.syntax.ast.ExpId;
import minijava.syntax.ast.ExpIntConst;
import minijava.syntax.ast.ExpInvoke;
import minijava.syntax.ast.ExpNeg;
import minijava.syntax.ast.ExpNew;
import minijava.syntax.ast.ExpNewIntArray;
import minijava.syntax.ast.ExpThis;
import minijava.syntax.ast.ExpTrue;
import minijava.syntax.ast.ExpVisitor;
import minijava.syntax.ast.Prg;
import minijava.syntax.ast.StmArrayAssign;
import minijava.syntax.ast.StmAssign;
import minijava.syntax.ast.StmIf;
import minijava.syntax.ast.StmList;
import minijava.syntax.ast.StmPrintChar;
import minijava.syntax.ast.StmPrintlnInt;
import minijava.syntax.ast.StmVisitor;
import minijava.syntax.ast.StmWhile;

public class IntermediateVisitor {

  public static Object prettyPrint(Prg p) {
	  return null;
  }

  private static Object prettyPrintClass(DeclClass c, String indent) {
	  return null;
  }

  private static Object prettyPrintClassList(List<DeclClass> cl, String indent) {
	  return null;
  }

  private static Object prettyPrintMeth(DeclMeth m, String indent) {
	  return null;
  }

  private static Object prettyPrintMethList(List<DeclMeth> dm, String indent) {
	  return null;
  }

  private static Object prettyPrintVar(DeclVar d, String indent) {
	  return null;
  }

  private static Object prettyPrintVarList(List<DeclVar> dl, String indent) {
	  return null;
  }

  private static Object prettyPrintMain(DeclMain d, String indent) {
	  return null;
  }

  /*static class IntermediateVisitorTy implements TyVisitor<String> {

    @Override
    public String visit(TyVoid b) {
    }

    @Override
    public String visit(TyBool b) {
    }

    @Override
    public String visit(TyInt i) {
    }

    @Override
    public String visit(TyClass x) {
    }

    @Override
    public String visit(TyArr x) {
    }
  }*/

  static class IntermediateVisitorExp implements ExpVisitor<TreeExp, RuntimeException> {

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
		return new TreeExpCALL(new TreeExpNAME(new Label("L_halloc")), Collections.singletonList(arraySize));
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
			throw new IllegalArgumentException("Unable to negate the expression \""+e.prettyPrint()+"\"");
		}
		TreeExpCONST negatedBoolean = (TreeExpCONST) negatedExpression;
		assert(negatedBoolean.value == 0 || negatedBoolean.value == 1);
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
			throw new IllegalArgumentException("Unknown operator: "+e.op);
		}
		return new TreeExpOP(operator, e.left.accept(this), e.right.accept(this));
	}

	@Override
	public TreeExp visit(ExpArrayGet e) throws RuntimeException {
		// MEM(BinOp(+, translate(array), BinOp(*, translate(index), BinOp(+, WORD_SIZE, CONST(1))))
		/*TreeExp array = e.array.accept(this);
		TreeExp index = e.index.accept(this);
		new TreeExpMEM(addr);*/
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
		/*TreeExp object = e.obj.accept(this);
		if (!(object instanceof TreeExpCONST)) {
			throw new RuntimeException("Unable to invoke method on object \""+e.obj.prettyPrint()+"\"");
		}*/
		String className = "";
		String methodName = e.method;
		// FIXME: Retrieve function label with the respective mangled name
		TreeExp function = new TreeExpNAME(new Label(mangle(className, methodName)));
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
		return className+"$"+methodName;
	}
  }

  static class IntermediateVisitorStm implements StmVisitor<TreeStm, RuntimeException> {

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
