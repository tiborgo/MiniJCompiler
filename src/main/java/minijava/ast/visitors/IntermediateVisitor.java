package minijava.ast.visitors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
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
import minijava.ast.rules.Stm;
import minijava.ast.rules.StmArrayAssign;
import minijava.ast.rules.StmAssign;
import minijava.ast.rules.StmIf;
import minijava.ast.rules.StmList;
import minijava.ast.rules.StmPrintChar;
import minijava.ast.rules.StmPrintlnInt;
import minijava.ast.rules.StmWhile;
import minijava.ast.rules.Ty;
import minijava.ast.rules.TyArr;
import minijava.ast.rules.TyBool;
import minijava.ast.rules.TyClass;
import minijava.ast.rules.TyInt;
import minijava.backend.MachineSpecifics;
import minijava.intermediate.Fragment;
import minijava.intermediate.FragmentProc;
import minijava.intermediate.Frame;
import minijava.intermediate.Label;
import minijava.intermediate.Temp;
import minijava.intermediate.tree.TreeExp;
import minijava.intermediate.tree.TreeExpCALL;
import minijava.intermediate.tree.TreeExpCONST;
import minijava.intermediate.tree.TreeExpESEQ;
import minijava.intermediate.tree.TreeExpMEM;
import minijava.intermediate.tree.TreeExpNAME;
import minijava.intermediate.tree.TreeExpOP;
import minijava.intermediate.tree.TreeExpOP.Op;
import minijava.intermediate.tree.TreeExpTEMP;
import minijava.intermediate.tree.TreeStm;
import minijava.intermediate.tree.TreeStmCJUMP;
import minijava.intermediate.tree.TreeStmCJUMP.Rel;
import minijava.intermediate.tree.TreeStmEXP;
import minijava.intermediate.tree.TreeStmJUMP;
import minijava.intermediate.tree.TreeStmLABEL;
import minijava.intermediate.tree.TreeStmMOVE;
import minijava.intermediate.tree.TreeStmSEQ;
import minijava.symboltable.tree.Program;
import minijava.symboltable.tree.Variable;

public class IntermediateVisitor implements
	PrgVisitor<List<Fragment<TreeStm>>, RuntimeException>, 
	DeclVisitor<List<Fragment<TreeStm>>, RuntimeException> {

	private String contextClassName;
	private final MachineSpecifics  machineSpecifics;
	private final Map<String, TreeExpTEMP> classTemps;
	private final Map<String, TreeExpTEMP> methodTemps;

	public IntermediateVisitor(MachineSpecifics machineSpecifics) {
		this.machineSpecifics = machineSpecifics;
		classTemps = new HashMap<>();
		methodTemps = new HashMap<>();
	}
	
	@Override
	public List<Fragment<TreeStm>> visit(Prg p) throws RuntimeException {
		
		// TODO: add main class
		List<Fragment<TreeStm>> classes = new LinkedList<>();
		for(DeclClass clazz : p.classes) {
			classes.addAll(clazz.accept(this));
		}
		return classes;
	}

	@Override
	public List<Fragment<TreeStm>> visit(DeclClass c) throws RuntimeException {
		this.contextClassName = c.className;

		// Fields
		classTemps.clear();
		for (DeclVar variableDeclaration : c.fields) {
			classTemps.put(variableDeclaration.name, new TreeExpTEMP(new Temp()));
		}

		// Methods
		List<Fragment<TreeStm>> methods = new LinkedList<>();
		for(DeclMeth method : c.methods) {
			methods.addAll(method.accept(this));
		}

		this.contextClassName = null;
		
		return methods;
	}

	@Override
	public List<Fragment<TreeStm>> visit(DeclMain d) throws RuntimeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Fragment<TreeStm>> visit(DeclMeth m) throws RuntimeException {
		Frame frame = this.machineSpecifics.newFrame(new Label(mangle(this.contextClassName, m.methodName)), m.parameters.size());

		methodTemps.clear();
		for (int i = 0; i < m.parameters.size(); i++) {
			methodTemps.put(m.parameters.get(i).id, (TreeExpTEMP) frame.getParameter(i));
		}
		for (DeclVar var : m.localVars) {
			methodTemps.put(var.name, new TreeExpTEMP(new Temp()));
		}

		Map<String, TreeExpTEMP> methodAndClassTemps = new HashMap<>();
		methodAndClassTemps.putAll(classTemps);
		methodAndClassTemps.putAll(methodTemps);
		TreeStm body = m.body.accept(new IntermediateVisitorExpStm(methodAndClassTemps, machineSpecifics));
		
		Fragment<TreeStm> frag = new FragmentProc<>(frame, body);
		
		return Arrays.asList(frag);
	}
	
	@Override
	public List<Fragment<TreeStm>> visit(DeclVar d) throws RuntimeException {
		throw new UnsupportedOperationException("Cannot generate fragment for var declaration");
	}
	
	private static String mangle(String className, String methodName) {
		return className + "$" + methodName;
	}

	static public class IntermediateVisitorExpStm implements
		ExpVisitor<TreeExp, RuntimeException>,
		StmVisitor<TreeStm, RuntimeException> {
		
	
		// FIXME: Symbol table is always null
		private Program symbolTable;
		private final Map<String, TreeExpTEMP> temps;
		private final MachineSpecifics  machineSpecifics;
		
		public IntermediateVisitorExpStm(Map<String, TreeExpTEMP> temps, MachineSpecifics machineSpecifics) {
			this.temps = temps;
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
			TreeExp arraySize = new TreeExpOP(
					Op.PLUS,
					e.size.accept(this),
					new TreeExpCONST(1)
			);
			Temp arrayMemoryLocation = new Temp();
			TreeExp memoryAllocation = new TreeExpCALL(new TreeExpNAME(new Label("L_halloc")), Collections.singletonList(arraySize));
			TreeStm writeArrayLength = new TreeStmMOVE(new TreeExpTEMP(arrayMemoryLocation), arraySize);
			return new TreeExpESEQ(
					TreeStmSEQ.fromArray(
							new TreeStmMOVE(new TreeExpTEMP(arrayMemoryLocation), memoryAllocation),
							writeArrayLength
					),
					new TreeExpTEMP(arrayMemoryLocation)
			);
		}
	
		@Override
		public TreeExp visit(ExpNew e) throws RuntimeException {
			minijava.symboltable.tree.Class clazz = symbolTable.classes.get(e.className);
			int classMemoryFootprint = 0;
			for (Variable field : clazz.fields.values()) {
				// FIXME: use actual word size
				classMemoryFootprint += getMemoryFootprint(field);
			}
	
			// Allocate space according to the size of the class
			TreeExp classMemoryFootprintExp = new TreeExpCONST(classMemoryFootprint);
			return new TreeExpCALL(new TreeExpNAME(new Label("L_halloc")),
					Collections.singletonList(classMemoryFootprintExp));
		}
	
		private int getMemoryFootprint(Variable variable) {
			int size = 0;
			Ty type = variable.type;
			if (type instanceof TyInt) {
				size += 4;
			} else if (type instanceof TyBool) {
				size += 4;
			} else if (type instanceof TyArr) {
				size += 4;
			} else if (type instanceof TyClass) {
				size += 4;
			}
			return size;
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
					/*TreeExp subtractionResult = new TreeExpOP(Op.MINUS, e.left.accept(this), e.right.accept(this));
					TreeExp smallerThan = new TreeExpOP(
							Op.LSHIFT,
							new TreeExpOP(Op.AND, new TreeExpCONST(0x10000000), subtractionResult),
							new TreeExpCONST(7)
					);
					return smallerThan;*/
					Temp result = new Temp();
					Label jumpPointTrue = new Label();
					Label jumpPointFalse = new Label();
					return new TreeExpESEQ(
							TreeStm.fromArray(
									new TreeStmMOVE(new TreeExpTEMP(result), new TreeExpCONST(0)),
									new TreeStmCJUMP(
											TreeStmCJUMP.Rel.LT,
											e.left.accept(this),
											e.right.accept(this),
											jumpPointTrue,
											jumpPointFalse
									),
									new TreeStmLABEL(jumpPointTrue),
									new TreeStmMOVE(new TreeExpTEMP(result), new TreeExpCONST(1)),
									new TreeStmLABEL(jumpPointFalse)
							),
							new TreeExpTEMP(result)
					);
				default:
					throw new IllegalArgumentException("Unknown operator: " + e.op);
			}
			return new TreeExpOP(operator, e.left.accept(this),
					e.right.accept(this));
		}
	
		@Override
		public TreeExp visit(ExpArrayGet e) throws RuntimeException {
			TreeExp array = e.array.accept(this);
			TreeExp index = e.index.accept(this);
			TreeExp arrayOffset = new TreeExpOP(Op.MUL, index, new TreeExpCONST(4));
			TreeExp memoryLocation = new TreeExpOP(
					Op.PLUS,
					new TreeExpOP(Op.PLUS, array, arrayOffset),
					new TreeExpCONST(1)
			);
			return new TreeExpMEM(memoryLocation);
		}
	
		@Override
		public TreeExp visit(ExpArrayLength e) throws RuntimeException {
			TreeExp array = e.array.accept(this);
			return new TreeExpMEM(array);
		}
	
		@Override
		public TreeExp visit(ExpInvoke e) throws RuntimeException {
			/*
			 * TreeExp object = e.obj.accept(this); if (!(object instanceof
			 * TreeExpCONST)) { throw new
			 * RuntimeException("Unable to invoke method on object \""
			 * +e.obj.prettyPrint()+"\""); }
			 */
			TreeExp object = e.obj.accept(this);
			// TODO Retrieve class name
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
			return new TreeExpCALL(function, arguments);
		}
	
		@Override
		public TreeExp visit(ExpIntConst e) throws RuntimeException {
			return new TreeExpCONST(e.value);
		}
	
		@Override
		public TreeExp visit(ExpId e) throws RuntimeException {
			return temps.get(e.id);
		}
	
		@Override
		public TreeStm visit(StmList s) throws RuntimeException {
			List<TreeStm> statementList = new ArrayList<>(s.stms.size());
			for (Stm statement : s.stms) {
				statementList.add(statement.accept(this));
			}
			return TreeStm.fromList(statementList);
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
	
			TreeExpTEMP dest = this.temps.get(s.id);
	
			if (dest != null) {
				return new TreeStmMOVE(dest, s.rhs.accept(this));
			}
			else {
				// TODO: error
				return null;
			}
		}
	
		@Override
		public TreeStm visit(StmArrayAssign s) throws RuntimeException {
	
			TreeExpTEMP array = this.temps.get(s.id);
			//TreeExp arrayExp = new TreeExpTEMP(array);
	
			TreeExp indexExp = s.index.accept(this);
			TreeExp assignExp = s.rhs.accept(this);
	
			Temp indexTemp = new Temp();
			TreeExpTEMP indexTempExp = new TreeExpTEMP(indexTemp);
	
			return TreeStm.fromArray(new TreeStm[]{
				// Calculate address of array item
				new TreeStmMOVE(indexTempExp, indexExp),
				new TreeStmMOVE(indexTempExp, new TreeExpOP(Op.PLUS, indexTempExp, new TreeExpCONST(1))),
				new TreeStmMOVE(indexTempExp, new TreeExpOP(Op.MUL, indexTempExp, new TreeExpCONST(this.machineSpecifics.getWordSize()))),
				new TreeStmMOVE(indexTempExp, new TreeExpOP(Op.PLUS, indexTempExp, array)),
	
				// Assign array item value
				new TreeStmMOVE(new TreeExpMEM(indexTempExp), assignExp)
			});
	
		}
	}
}
