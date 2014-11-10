package minijava.ast.visitors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
import minijava.backend.MachineSpecifics;
import minijava.intermediate.Label;
import minijava.intermediate.Temp;
import minijava.intermediate.tree.TreeExp;
import minijava.intermediate.tree.TreeExpCALL;
import minijava.intermediate.tree.TreeExpCONST;
import minijava.intermediate.tree.TreeExpMEM;
import minijava.intermediate.tree.TreeExpNAME;
import minijava.intermediate.tree.TreeExpOP;
import minijava.intermediate.tree.TreeExpTEMP;
import minijava.intermediate.tree.TreeStmMOVE;
import minijava.intermediate.tree.TreeExpOP.Op;
import minijava.intermediate.tree.TreeStm;
import minijava.intermediate.tree.TreeStmCJUMP;
import minijava.intermediate.tree.TreeStmCJUMP.Rel;
import minijava.intermediate.tree.TreeStmEXP;
import minijava.intermediate.tree.TreeStmJUMP;
import minijava.intermediate.tree.TreeStmLABEL;

public class IntermediateVisitor implements
	ExpVisitor<TreeExp, RuntimeException>,
	StmVisitor<TreeStm, RuntimeException> {

	/*
	 * static class IntermediateVisitorTy implements TyVisitor<String> {
	 * 
	 * @Override public String visit(TyVoid b) { }
	 * 
	 * @Override public String visit(TyBool b) { }
	 * 
	 * @Override public String visit(TyInt i) { }
	 * 
	 * @Override public String visit(TyClass x) { }
	 * 
	 * @Override public String visit(TyArr x) { } }
	 */
	
	private final Map<String, Temp> localVariables;
	private final MachineSpecifics  machineSpecifics;
	
	public IntermediateVisitor(Map<String, Temp> localVariables, MachineSpecifics machineSpecifics) {
		this.localVariables = localVariables;
		this.machineSpecifics = machineSpecifics;
	}

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
						"Unable to negate the expression \"" + e.accept(new PrettyPrintVisitor.PrettyPrintVisitorExp())
								+ "\"");
			}
			TreeExpCONST negatedBoolean = (TreeExpCONST) negatedExpression;
			assert (negatedBoolean.value == 0 || negatedBoolean.value == 1);
			return new TreeExpCONST(1 - negatedBoolean.value);
		}

		@Override
		public 	 visit(ExpBinOp e) throws RuntimeException {
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

		@Override
		public TreeStm visit(StmList s) throws RuntimeException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public TreeStm visit(StmIf s) throws RuntimeException {
			
			Label trueLabel  = new Label();
			Label falseLabel = new Label();
			Label afterLabel = new Label();
			
			return TreeStm.fromArray(new TreeStm[]{
						new TreeStmCJUMP(Rel.EQ,
								s.cond.accept(this),
								new TreeExpCONST(1),
								trueLabel,
								falseLabel),
						new TreeStmLABEL(trueLabel),
						s.bodyTrue.accept(this),
						TreeStmJUMP.jumpToLabel(afterLabel),
						new TreeStmLABEL(falseLabel),
						s.bodyFalse.accept(this),
						new TreeStmLABEL(afterLabel)
				});
		}

		@Override
		public TreeStm visit(StmWhile s) throws RuntimeException {
			
			Label beforeLabel = new Label(); 
			Label bodyLabel   = new Label();
			Label afterLabel  = new Label();
			
			
			
			return TreeStm.fromArray(new TreeStm[]{
				new TreeStmLABEL(beforeLabel),
				new TreeStmCJUMP(Rel.EQ,
						s.cond.accept(this),
						new TreeExpCONST(1),
						bodyLabel,
						afterLabel),
				new TreeStmLABEL(bodyLabel),
				s.body.accept(this),
				TreeStmJUMP.jumpToLabel(beforeLabel),
				new TreeStmLABEL(afterLabel)
		});
		}

		@Override
		public TreeStm visit(StmPrintlnInt s) throws RuntimeException {
			
			Label printlnIntLabel = new Label("L_println_int");
			
			return new TreeStmEXP(
					new TreeExpCALL(
							new TreeExpNAME(printlnIntLabel), 
							Arrays.asList(s.arg.accept(this))
							)
					);
		}

		@Override
		public TreeStm visit(StmPrintChar s) throws RuntimeException {
			
			Label printCharLabel = new Label("L_print_char");
			
			return new TreeStmEXP(
					new TreeExpCALL(
							new TreeExpNAME(printCharLabel), 
							Arrays.asList(s.arg.accept(this))
							)
					);
		}

		@Override
		public TreeStm visit(StmAssign s) throws RuntimeException {
			
			Temp dest = this.localVariables.get(s.id);
			
			if (dest != null) {
				return new TreeStmMOVE(new TreeExpTEMP(dest), s.rhs.accept(this));
			}
			else {
				// TODO: error
				return null;
			}
		}

		@Override
		public TreeStm visit(StmArrayAssign s) throws RuntimeException {
			
			Temp array = this.localVariables.get(s.id);
			TreeExp arrayExp = new TreeExpTEMP(array);
			
			TreeExp indexExp = s.index.accept(this);
			TreeExp assignExp = s.rhs.accept(this);
			
			Temp indexTemp = new Temp();
			TreeExpTEMP indexTempExp = new TreeExpTEMP(indexTemp);
			
			return TreeStm.fromArray(new TreeStm[]{
				// Calculate address of array item
				new TreeStmMOVE(indexTempExp, indexExp),
				new TreeStmMOVE(indexTempExp, new TreeExpOP(Op.PLUS, indexTempExp, new TreeExpCONST(1))),
				new TreeStmMOVE(indexTempExp, new TreeExpOP(Op.MUL, indexTempExp, new TreeExpCONST(this.machineSpecifics.getWordSize()))),
				new TreeStmMOVE(indexTempExp, new TreeExpOP(Op.PLUS, indexTempExp, arrayExp)),
				
				// Assign array item value
				new TreeStmMOVE(new TreeExpMEM(indexTempExp), assignExp)
			});
			
		}
}
