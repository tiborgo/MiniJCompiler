package minijava.intermediate;

import java.util.List;

import minijava.intermediate.tree.TreeExp;
import minijava.syntax.ast.ExpVisitor;
import minijava.syntax.ast.StmArrayAssign;
import minijava.syntax.ast.StmAssign;
import minijava.syntax.ast.StmIf;
import minijava.syntax.ast.StmList;
import minijava.syntax.ast.StmPrintChar;
import minijava.syntax.ast.StmPrintlnInt;
import minijava.syntax.ast.StmVisitor;
import minijava.syntax.ast.StmWhile;

public class IntermediatePrint {

  private static final String indentStep = "  ";

  public static String prettyPrint(Prg p) {
  }

  private static String prettyPrintClass(DeclClass c, String indent) {
  }

  private static String prettyPrintClassList(List<DeclClass> cl, String indent) {
  }

  private static String prettyPrintMeth(DeclMeth m, String indent) {
  }

  private static String prettyPrintMethList(List<DeclMeth> dm, String indent) {
  }

  private static String prettyPrintVar(DeclVar d, String indent) {
  }

  private static String prettyPrintVarList(List<DeclVar> dl, String indent) {
  }

  private static String prettyPrintMain(DeclMain d, String indent) {
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
    public TreeExp visit(ExpTrue x) {
    }

    @Override
    public TreeExp visit(ExpFalse x) {
    }

    @Override
    public TreeExp visit(ExpThis x) {
    }

    @Override
    public TreeExp visit(ExpNewIntArray x) {
    }

    @Override
    public TreeExp visit(ExpNew x) {
    }

    @Override
    public TreeExp visit(ExpBinOp e) {
    }

    @Override
    public TreeExp visit(ExpArrayGet e) {
    }

    @Override
    public TreeExp visit(ExpArrayLength e) {
    }

    @Override
    public TreeExp visit(ExpInvoke e) {
    }

    @Override
    public TreeExp visit(ExpIntConst x) {
    }

    @Override
    public TreeExp visit(ExpId x) {
    }

    @Override
    public TreeExp visit(ExpNeg x) {
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
