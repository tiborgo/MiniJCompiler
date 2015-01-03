package minijava.intermediate.visitors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import minijava.ast.rules.declarations.DeclClass;
import minijava.ast.rules.declarations.DeclMain;
import minijava.ast.rules.declarations.DeclMeth;
import minijava.ast.rules.declarations.DeclVar;
import minijava.ast.rules.declarations.DeclVisitor;
import minijava.ast.rules.Parameter;
import minijava.ast.rules.Prg;
import minijava.ast.rules.PrgVisitor;
import minijava.ast.rules.expressions.Exp;
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
import minijava.ast.rules.types.TyArr;
import minijava.ast.rules.types.TyClass;
import minijava.ast.rules.types.TyInt;
import minijava.symboltable.visitors.TypeCheckVisitor;
import minijava.backend.MachineSpecifics;
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
import minijava.symboltable.tree.Class;
import minijava.symboltable.tree.Method;
import minijava.symboltable.tree.Program;

public class IntermediateVisitor implements
		PrgVisitor<List<FragmentProc<TreeStm>>, RuntimeException>,
		DeclVisitor<List<FragmentProc<TreeStm>>, RuntimeException> {
	
	private DeclClass classContext;
	private DeclMeth methodContext;
	private final MachineSpecifics  machineSpecifics;
	private final Map<String, TreeExp> classTemps;
	private Map<String, Integer> memoryFootprint;
	private final Program symbolTable;

	public IntermediateVisitor(MachineSpecifics machineSpecifics, Program symbolTable) {
		this.machineSpecifics = machineSpecifics;
		classTemps = new HashMap<>();
		this.symbolTable = symbolTable;
		this.memoryFootprint = new HashMap<>();
	}

	@Override
	public List<FragmentProc<TreeStm>> visit(Prg p) throws RuntimeException {

		for(DeclClass clazz : p.classes) {
			memoryFootprint.put(clazz.className, clazz.fields.size() * machineSpecifics.getWordSize() + 4);
		}

		List<FragmentProc<TreeStm>> classes = new LinkedList<>();
		for(DeclClass clazz : p.classes) {
			classes.addAll(clazz.accept(this));
		}

		classes.addAll(p.mainClass.accept(this));
		return classes;
	}

	@Override
	public List<FragmentProc<TreeStm>> visit(DeclClass c) throws RuntimeException {
		classContext = c;

		// Methods
		List<FragmentProc<TreeStm>> methods = new LinkedList<>();
		for(DeclMeth method : c.methods) {
			methods.addAll(method.accept(this));
		}

		classContext = null;

		return methods;
	}

	@Override
	public List<FragmentProc<TreeStm>> visit(DeclMain d) throws RuntimeException {

		DeclMeth mainMethod = new DeclMeth(
			new TyInt(),
			"lmain",
			Arrays.asList(new Parameter(d.mainArg, new TyArr(new TyInt()))),
			Collections.<DeclVar>emptyList(),
			d.mainBody,
			new ExpIntConst(0)
		);
		
		DeclClass mainClass = new DeclClass(
			"",
			null,
			Collections.<DeclVar>emptyList(),
			Arrays.asList(mainMethod)
		);

		return mainClass.accept(this);
	}

	@Override
	public List<FragmentProc<TreeStm>> visit(DeclMeth m) throws RuntimeException {

		methodContext = m;

		Frame frame = this.machineSpecifics.newFrame(new Label(mangle(classContext.className, m.methodName)), m.parameters.size() + 1);

		Map<String, TreeExp> methodTemps = new HashMap<>();
		
		TreeExp thisExp = frame.getParameter(0);
		
		methodTemps.put("this", thisExp);
		for (int i = 1; i < m.parameters.size()+1; i++) {
			methodTemps.put(m.parameters.get(i-1).id, frame.getParameter(i));
		}
		for (DeclVar var : m.localVars) {
			methodTemps.put(var.name, frame.addLocal(Frame.Location.ANYWHERE));
		}
		
		// Fields
		// TODO: better in DeclClass visit ?
		classTemps.clear();
		for (int i = 0; i < classContext.fields.size(); i++) {
			int offset = (i+1) * machineSpecifics.getWordSize();
			classTemps.put(classContext.fields.get(i).name, new TreeExpMEM(new TreeExpOP(Op.PLUS, thisExp, new TreeExpCONST(offset))));
		}

		Map<String, TreeExp> methodAndClassTemps = new HashMap<>();
		methodAndClassTemps.putAll(classTemps);
		methodAndClassTemps.putAll(methodTemps);
		TreeStm body = m.body.accept(new IntermediateVisitorExpStm(methodAndClassTemps, machineSpecifics, classContext, methodContext, memoryFootprint, symbolTable));
		TreeExp returnExp = m.returnExp.accept(new IntermediateVisitorExpStm(methodAndClassTemps, machineSpecifics, classContext, methodContext, memoryFootprint, symbolTable));

		TreeStm method = frame.makeProc(body, returnExp);

		FragmentProc<TreeStm> frag = new FragmentProc<>(frame, method);

		methodContext = null;

		return Arrays.asList(frag);
	}

	@Override
	public List<FragmentProc<TreeStm>> visit(DeclVar d) throws RuntimeException {
		throw new UnsupportedOperationException("Cannot generate fragment for var declaration");
	}

	private static String mangle(String className, String methodName) {
		return (className != "") ? className + "$" + methodName : methodName;
	}

	static public class IntermediateVisitorExpStm implements
			ExpVisitor<TreeExp, RuntimeException>,
			StmVisitor<TreeStm, RuntimeException> {

		private final Program symbolTable;
		private final DeclClass classContext;
		private final DeclMeth methodContext;
		private final Map<String, Integer> memoryFootprint;
		private final Map<String, TreeExp> temps;
		private final MachineSpecifics  machineSpecifics;

		public IntermediateVisitorExpStm(Map<String, TreeExp> temps,
				MachineSpecifics machineSpecifics,
				DeclClass classContext,
				DeclMeth methodContext,
				Map<String, Integer> memoryFootprint,
				Program symbolTable) {
			this.temps = temps;
			this.machineSpecifics = machineSpecifics;
			this.classContext = classContext;
			this.methodContext = methodContext;
			this.memoryFootprint = memoryFootprint;
			this.symbolTable = symbolTable;
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
			return temps.get("this");
		}

		@Override
		public TreeExp visit(ExpNewIntArray e) throws RuntimeException {
			
			TreeExp arraySize = e.size.accept(this);
			
			TreeExp arrayMemorySize = new TreeExpOP(
					Op.PLUS,
					new TreeExpOP(Op.MUL, arraySize, new TreeExpCONST(machineSpecifics.getWordSize())),
					new TreeExpCONST(machineSpecifics.getWordSize())
			);
			TreeExpTEMP arrayMemoryLocation = new TreeExpTEMP(new Temp());
			TreeExp memoryAllocation = new TreeExpCALL(new TreeExpNAME(new Label("halloc")), Collections.singletonList(arrayMemorySize));
			TreeStm writeArrayLength = new TreeStmMOVE(new TreeExpMEM(arrayMemoryLocation), arraySize);
			return new TreeExpESEQ(
					TreeStmSEQ.fromArray(
							new TreeStmMOVE(arrayMemoryLocation, memoryAllocation),
							writeArrayLength
					),
					arrayMemoryLocation
			);
		}

		@Override
		public TreeExp visit(ExpNew e) throws RuntimeException {
			int classMemoryFootprint = memoryFootprint.get(e.className);

			// Allocate space according to the size of the class
			TreeExp classMemoryFootprintExp = new TreeExpCONST(classMemoryFootprint);
			return new TreeExpCALL(new TreeExpNAME(new Label("halloc")),
					Collections.singletonList(classMemoryFootprintExp));
			
			// TODO: set class ID
		}

		@Override
		public TreeExp visit(ExpNeg e) throws RuntimeException {
			TreeExp negatedExpression = e.body.accept(this);
			return negateExpression(negatedExpression);
		}

		private TreeStm negateStatement(TreeStm statement) {
			if (statement instanceof TreeStmEXP) {
				TreeExp expression = ((TreeStmEXP) statement).exp;
				return new TreeStmEXP(negateExpression(expression));
			} else if (statement instanceof TreeStmSEQ) {
				TreeStmSEQ sequentialStatement = (TreeStmSEQ) statement;
				return new TreeStmSEQ(negateStatement(sequentialStatement.first), negateStatement(sequentialStatement.second));
			} else if (statement instanceof TreeStmCJUMP) {
				TreeStmCJUMP conditionalJump = (TreeStmCJUMP) statement;
				return new TreeStmCJUMP(conditionalJump.rel.neg(), conditionalJump.left, conditionalJump.right,
						conditionalJump.ltrue, conditionalJump.lfalse);
			}
			return statement;
		}

		private TreeExp negateExpression(TreeExp expression) {
			if (expression instanceof TreeExpCONST) {
				TreeExpCONST negatedBoolean = (TreeExpCONST) expression;
				assert (negatedBoolean.value == 0 || negatedBoolean.value == 1);
				return new TreeExpCONST(1 - negatedBoolean.value);
			} else if (expression instanceof TreeExpESEQ) {
				TreeExpESEQ negatedESEQ = (TreeExpESEQ) expression;
				return new TreeExpESEQ(negateStatement(negatedESEQ.stm), negatedESEQ.res);
			} else if (expression instanceof TreeExpCALL) {
				TreeExpCALL call = (TreeExpCALL) expression;
				return new TreeExpOP(Op.MINUS, call, new TreeExpCONST(1));
			} else if (expression instanceof TreeExpTEMP) {
				TreeExpTEMP temp = (TreeExpTEMP) expression;
				return new TreeExpOP(Op.MINUS, temp, new TreeExpCONST(1));
			} else if (expression instanceof TreeExpOP) {
				return negateOperator((TreeExpOP) expression);
			}
			throw new IllegalArgumentException("Unable to negate expression \"" + expression.toString() + "\"");
		}

		private TreeExpOP negateOperator(TreeExpOP operation) {
			Op negatedOperator = null;
			switch (operation.op) {
				case AND:
					negatedOperator = Op.OR;
					break;
				case OR:
					negatedOperator = Op.AND;
					break;
				default:
					throw new IllegalArgumentException("Cannot negate operator "+operation.op);
			}
			return new TreeExpOP(negatedOperator, operation.left, operation.right);
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
					// FIXME: Op.AND does not equal &&
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
					// TODO: optimise (slide 148)
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
					
			// TODO: check array bounds
			
			return new TreeExpMEM(
				new TreeExpOP(
					Op.PLUS,
					new TreeExpOP(
						Op.MUL,
						new TreeExpOP(Op.PLUS, index, new TreeExpCONST(1)),
						new TreeExpCONST(machineSpecifics.getWordSize())
					),
					array
				)
			);
		}

		@Override
		public TreeExp visit(ExpArrayLength e) throws RuntimeException {
			TreeExp array = e.array.accept(this);
			return new TreeExpMEM(array);
		}

		@Override
		public TreeExp visit(ExpInvoke e) throws RuntimeException {
			
			TreeExp object = e.obj.accept(this);
			Class clazz = symbolTable.classes.get(classContext.className);
			Method method = clazz.methods.get(methodContext.methodName);
			String className = ((TyClass) e.obj.accept(new TypeCheckVisitor.TypeCheckVisitorExpTyStm(symbolTable, clazz, method))).c;
			String methodName = e.method;

			TreeExp function = new TreeExpNAME(new Label(mangle(className,
					methodName)));
			List<TreeExp> arguments = new ArrayList<>(e.args.size() + 1);
			arguments.add(object);
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

			Label printlnIntLabel = new Label("println_int");

			return new TreeStmEXP(
					new TreeExpCALL(
							new TreeExpNAME(printlnIntLabel),
							Arrays.asList(s.arg.accept(this))
							)
					);
		}

		@Override
		public TreeStm visit(StmPrintChar s) throws RuntimeException {

			Label printCharLabel = new Label("print_char");

			return new TreeStmEXP(
					new TreeExpCALL(
							new TreeExpNAME(printCharLabel),
							Arrays.asList(s.arg.accept(this))
							)
					);
		}

		@Override
		public TreeStm visit(StmAssign s) throws RuntimeException {

			TreeExp dest = this.temps.get(s.id);
			TreeExp assignValue = s.rhs.accept(this);

			return new TreeStmMOVE(dest, assignValue);
		}

		@Override
		public TreeStm visit(StmArrayAssign s) throws RuntimeException {
			
			TreeExp array = this.temps.get(s.id);
			TreeExp index = s.index.accept(this);
			TreeExp assignValue = s.rhs.accept(this);
			
			Label raiseLabel = new Label();
			Label assignLabel = new Label();
			
			TreeStm boundsCheckStm = new TreeStmCJUMP(
					Rel.GE,
					index,
					new TreeExpMEM(array),
					raiseLabel,
					assignLabel);

			// jump explicitly to label so that base block generator produces efficient base blocks
			TreeStm raiseStm = TreeStmSEQ.fromArray(
				new TreeStmEXP( 
					TreeExpCALL.call1("raise", new TreeExpCONST(1))
				),
				TreeStmJUMP.jumpToLabel(raiseLabel)
			);

			
			TreeStm assignStm =  new TreeStmMOVE(
				new TreeExpMEM(
					new TreeExpOP(
						Op.PLUS,
						new TreeExpOP(
							Op.MUL,
							new TreeExpOP(
								Op.PLUS,
								index,
								new TreeExpCONST(1)
							),
							new TreeExpCONST(this.machineSpecifics.getWordSize())
						),
						array)
					),
				assignValue);

			return TreeStmSEQ.fromArray(
				boundsCheckStm,
				new TreeStmLABEL(raiseLabel),
				raiseStm,
				new TreeStmLABEL(assignLabel),
				assignStm
			);
		}
	}
}
