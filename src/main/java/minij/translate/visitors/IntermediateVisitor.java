/*
 * MiniJCompiler: Compiler for a subset of the Java(R) language
 *
 * (C) Copyright 2014-2015 Tibor Goldschwendt <goldschwendt@cip.ifi.lmu.de>,
 * Michael Seifert <mseifert@error-reports.org>
 *
 * This file is part of MiniJCompiler.
 *
 * MiniJCompiler is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MiniJCompiler is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MiniJCompiler.  If not, see <http://www.gnu.org/licenses/>.
 */
package minij.translate.visitors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import minij.instructionselection.MachineSpecifics;
import minij.parse.rules.Program;
import minij.parse.rules.ProgramVisitor;
import minij.parse.rules.declarations.DeclarationVisitor;
import minij.parse.rules.declarations.Main;
import minij.parse.rules.declarations.MainMethod;
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
import minij.translate.layout.FragmentProc;
import minij.translate.layout.Frame;
import minij.translate.layout.Label;
import minij.translate.layout.Temp;
import minij.translate.tree.TreeExp;
import minij.translate.tree.TreeExpCALL;
import minij.translate.tree.TreeExpCONST;
import minij.translate.tree.TreeExpESEQ;
import minij.translate.tree.TreeExpMEM;
import minij.translate.tree.TreeExpNAME;
import minij.translate.tree.TreeExpOP;
import minij.translate.tree.TreeExpTEMP;
import minij.translate.tree.TreeStm;
import minij.translate.tree.TreeStmCJUMP;
import minij.translate.tree.TreeStmEXP;
import minij.translate.tree.TreeStmJUMP;
import minij.translate.tree.TreeStmLABEL;
import minij.translate.tree.TreeStmMOVE;
import minij.translate.tree.TreeStmSEQ;
import minij.translate.tree.TreeExpOP.Op;
import minij.translate.tree.TreeStmCJUMP.Rel;

public class IntermediateVisitor implements
		ProgramVisitor<List<FragmentProc<TreeStm>>, RuntimeException>,
		DeclarationVisitor<List<FragmentProc<TreeStm>>, RuntimeException> {
	
	private minij.parse.rules.declarations.Class classContext;
	private Method methodContext;
	private final MachineSpecifics  machineSpecifics;
	private final Map<String, TreeExp> classTemps;
	private Map<String, java.lang.Integer> memoryFootprint;
	private final Program symbolTable;

	public IntermediateVisitor(MachineSpecifics machineSpecifics, Program symbolTable) {
		this.machineSpecifics = machineSpecifics;
		classTemps = new HashMap<>();
		this.symbolTable = symbolTable;
		this.memoryFootprint = new HashMap<>();
	}

	@Override
	public List<FragmentProc<TreeStm>> visit(Program p) throws RuntimeException {

		for(minij.parse.rules.declarations.Class clazz : p.getClasses()) {
			memoryFootprint.put(clazz.className, clazz.fields.size() * machineSpecifics.getWordSize() + 4);
		}

		List<FragmentProc<TreeStm>> classes = new LinkedList<>();
		for(minij.parse.rules.declarations.Class clazz : p.getClasses()) {
			classes.addAll(clazz.accept(this));
		}

		return classes;
	}

	@Override
	public List<FragmentProc<TreeStm>> visit(minij.parse.rules.declarations.Class c) throws RuntimeException {
		classContext = c;

		// Methods
		List<FragmentProc<TreeStm>> methods = new LinkedList<>();
		for(Method method : c.methods) {
			methods.addAll(method.accept(this));
		}

		classContext = null;

		return methods;
	}

	@Override
	public List<FragmentProc<TreeStm>> visit(Main d) throws RuntimeException {
		// Treat main class like a regular class
		return visit((minij.parse.rules.declarations.Class) d);
	}

	@Override
	public List<FragmentProc<TreeStm>> visit(Method m) throws RuntimeException {

		methodContext = m;
		Frame frame;

		Map<String, TreeExp> methodTemps = new HashMap<>();
		
		
		classTemps.clear();
		
		if (m instanceof MainMethod) {
			
			frame = this.machineSpecifics.newFrame(new Label("lmain"), m.parameters.size());
			
			for (int i = 0; i < m.parameters.size(); i++) {
				methodTemps.put(m.parameters.get(i).id, frame.getParameter(i));
			}
		}
		else {
			
			frame = this.machineSpecifics.newFrame(new Label(mangle(classContext.className, m.methodName)), m.parameters.size() + 1);
			
			TreeExp thisExp = frame.getParameter(0);
			methodTemps.put("this", thisExp);
			
			for (int i = 1; i < m.parameters.size()+1; i++) {
				methodTemps.put(m.parameters.get(i-1).id, frame.getParameter(i));
			}
			
			// Fields
			for (int i = 0; i < classContext.fields.size(); i++) {
				int offset = (i+1) * machineSpecifics.getWordSize();
				classTemps.put(classContext.fields.get(i).name, new TreeExpMEM(new TreeExpOP(Op.PLUS, thisExp, new TreeExpCONST(offset))));
			}
		}
		
		for (Variable var : m.localVars) {
			methodTemps.put(var.name, frame.addLocal(Frame.Location.ANYWHERE));
		}

		Label raiseLabel = new Label();
		Label afterLabel = new Label();
		TreeStm raiseStm = TreeStmSEQ.fromArray(
			TreeStmJUMP.jumpToLabel(afterLabel),
			new TreeStmLABEL(raiseLabel),
			new TreeStmEXP( 
				TreeExpCALL.call1("raise", new TreeExpCONST(1))
			),
			TreeStmJUMP.jumpToLabel(raiseLabel),
			new TreeStmLABEL(afterLabel)
		);
		
		Map<String, TreeExp> methodAndClassTemps = new HashMap<>();
		methodAndClassTemps.putAll(classTemps);
		methodAndClassTemps.putAll(methodTemps);
		IntermediateVisitorExpStm visitor = new IntermediateVisitorExpStm(methodAndClassTemps, machineSpecifics, classContext, methodContext, memoryFootprint, symbolTable, raiseLabel);
		TreeStm body = m.body.accept(visitor);
		TreeExp returnExp = m.returnExpression.accept(visitor);
		
		if (visitor.raises()) {
			body = new TreeStmSEQ(body, raiseStm);
		}
		
		TreeStm method = frame.makeProc(body, returnExp);

		FragmentProc<TreeStm> frag = new FragmentProc<>(frame, method);

		methodContext = null;

		return Arrays.asList(frag);
	}

	@Override
	public List<FragmentProc<TreeStm>> visit(Variable d) throws RuntimeException {
		throw new UnsupportedOperationException("Cannot generate fragment for var declaration");
	}

	private static String mangle(String className, String methodName) {
		return (className != "") ? className + "$" + methodName : methodName;
	}

	public static class IntermediateVisitorExpStm implements
			ExpressionVisitor<TreeExp, RuntimeException>,
			StatementVisitor<TreeStm, RuntimeException> {

		private final Map<String, java.lang.Integer> memoryFootprint;
		private final Map<String, TreeExp> temps;
		private final MachineSpecifics  machineSpecifics;
		private final Label raiseLabel;
		private boolean raises;
		
		public boolean raises() {
			return raises;
		}

		public IntermediateVisitorExpStm(Map<String, TreeExp> temps,
				MachineSpecifics machineSpecifics,
				minij.parse.rules.declarations.Class classContext,
				Method methodContext,
				Map<String, java.lang.Integer> memoryFootprint,
				Program symbolTable,
				Label raiseLabel) {
			
			this.temps = temps;
			this.machineSpecifics = machineSpecifics;
			this.memoryFootprint = memoryFootprint;
			this.raiseLabel = raiseLabel;
			raises = false;
		}

		@Override
		public TreeExp visit(True e) throws RuntimeException {
			return new TreeExpCONST(1);
		}

		@Override
		public TreeExp visit(False e) throws RuntimeException {
			return new TreeExpCONST(0);
		}

		@Override
		public TreeExp visit(This e) throws RuntimeException {
			return temps.get("this");
		}

		@Override
		public TreeExp visit(NewIntArray e) throws RuntimeException {
			
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
		public TreeExp visit(New e) throws RuntimeException {
			int classMemoryFootprint = memoryFootprint.get(e.className);

			// Allocate space according to the size of the class
			TreeExp classMemoryFootprintExp = new TreeExpCONST(classMemoryFootprint);
			return new TreeExpCALL(new TreeExpNAME(new Label("halloc")),
					Collections.singletonList(classMemoryFootprintExp));
			
			// TODO: set class ID
		}

		@Override
		public TreeExp visit(Negate e) throws RuntimeException {
			TreeExp negatedExpression = e.body.accept(this);
			return negateExpression(negatedExpression);
		}

		private TreeExp negateExpression(TreeExp expression) {
			if (expression instanceof TreeExpCONST) {
				TreeExpCONST negatedBoolean = (TreeExpCONST) expression;
				assert (negatedBoolean.value == 0 || negatedBoolean.value == 1);
				return new TreeExpCONST(1 - negatedBoolean.value);
			}
			else if (expression instanceof TreeExpESEQ) {
				TreeExpESEQ negatedESEQ = (TreeExpESEQ) expression;
				return new TreeExpESEQ(negatedESEQ.stm, negateExpression(negatedESEQ.res));
			}
			else if (expression instanceof TreeExpCALL) {
				TreeExpCALL call = (TreeExpCALL) expression;
				return new TreeExpOP(Op.MINUS, new TreeExpCONST(1), call);
			}
			else if (expression instanceof TreeExpTEMP) {
				TreeExpTEMP temp = (TreeExpTEMP) expression;
				return new TreeExpOP(Op.MINUS, new TreeExpCONST(1), temp);
			}
			else if (expression instanceof TreeExpOP) {
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
		public TreeExp visit(BinOp e) throws RuntimeException {
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
					
				case AND: {
					Temp result = new Temp();
					
					Label jumpPointSecondTest = new Label();
					Label jumpPointTrue = new Label();
					Label jumpPointFalse = new Label();
					
					return new TreeExpESEQ(
						TreeStm.fromArray(
							new TreeStmMOVE(new TreeExpTEMP(result), new TreeExpCONST(0)),
							new TreeStmCJUMP(
								Rel.EQ,
								e.left.accept(this),
								new TreeExpCONST(1),
								jumpPointSecondTest,
								jumpPointFalse
							),
							new TreeStmLABEL(jumpPointSecondTest),
							new TreeStmCJUMP(
								Rel.EQ,
								e.right.accept(this),
								new TreeExpCONST(1),
								jumpPointTrue,
								jumpPointFalse
							),
							new TreeStmLABEL(jumpPointTrue),
							new TreeStmMOVE(new TreeExpTEMP(result), new TreeExpCONST(1)),
							new TreeStmLABEL(jumpPointFalse)
						),
						new TreeExpTEMP(result)
					);
				}
				case LT: {
					
					Temp result = new Temp();
					Label jumpPointTrue = new Label();
					Label jumpPointFalse = new Label();
					return new TreeExpESEQ(
						TreeStm.fromArray(
							new TreeStmMOVE(new TreeExpTEMP(result), new TreeExpCONST(0)),
							new TreeStmCJUMP(
								Rel.LT,
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
				}
				default:
					throw new IllegalArgumentException("Unknown operator: " + e.op);
			}
			return new TreeExpOP(operator, e.left.accept(this),
					e.right.accept(this));
		}

		@Override
		public TreeExp visit(ArrayGet e) throws RuntimeException {
			TreeExp array = e.array.accept(this);
			TreeExp index = e.index.accept(this);
			
			Label getLabel = new Label();
			
			TreeStm boundsCheckStm = new TreeStmCJUMP(
				Rel.GE,
				index,
				new TreeExpMEM(array),
				raiseLabel,
				getLabel);
			
			raises = true;
			
			return new TreeExpESEQ(
				TreeStmSEQ.fromArray(
					boundsCheckStm,
					new TreeStmLABEL(getLabel)
				),
				new TreeExpMEM(
					new TreeExpOP(
						Op.PLUS,
						new TreeExpOP(
							Op.MUL,
							new TreeExpOP(Op.PLUS, index, new TreeExpCONST(1)),
							new TreeExpCONST(machineSpecifics.getWordSize())
						),
						array
					)
				)
			);
		}

		@Override
		public TreeExp visit(ArrayLength e) throws RuntimeException {
			TreeExp array = e.array.accept(this);
			return new TreeExpMEM(array);
		}

		@Override
		public TreeExp visit(Invoke e) throws RuntimeException {
			
			TreeExp object = e.obj.accept(this);
			
			String className = ((minij.parse.rules.types.Class) e.obj.type).c;
			String methodName = e.method;

			TreeExp function = new TreeExpNAME(new Label(mangle(className,
					methodName)));
			List<TreeExp> arguments = new ArrayList<>(e.args.size() + 1);
			arguments.add(object);
			for (Expression expression : e.args) {
				TreeExp iRExpression = expression.accept(this);
				arguments.add(iRExpression);
			}
			return new TreeExpCALL(function, arguments);
		}

		@Override
		public TreeExp visit(IntConstant e) throws RuntimeException {
			return new TreeExpCONST(e.value);
		}

		@Override
		public TreeExp visit(Id e) throws RuntimeException {
			return temps.get(e.id);
		}

		@Override
		public TreeStm visit(StatementList s) throws RuntimeException {
			List<TreeStm> statementList = new ArrayList<>(s.statements.size());
			for (Statement statement : s.statements) {
				statementList.add(statement.accept(this));
			}
			return TreeStm.fromList(statementList);
		}

		@Override
		public TreeStm visit(If s) throws RuntimeException {

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
		public TreeStm visit(While s) throws RuntimeException {

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
		public TreeStm visit(PrintlnInt s) throws RuntimeException {

			Label printlnIntLabel = new Label("println_int");

			return new TreeStmEXP(
					new TreeExpCALL(
							new TreeExpNAME(printlnIntLabel),
							Arrays.asList(s.arg.accept(this))
							)
					);
		}

		@Override
		public TreeStm visit(PrintChar s) throws RuntimeException {

			Label printCharLabel = new Label("print_char");

			return new TreeStmEXP(
					new TreeExpCALL(
							new TreeExpNAME(printCharLabel),
							Arrays.asList(s.arg.accept(this))
							)
					);
		}

		@Override
		public TreeStm visit(Assignment s) throws RuntimeException {

			TreeExp dest = this.temps.get(s.id.id);
			TreeExp assignValue = s.rhs.accept(this);

			return new TreeStmMOVE(dest, assignValue);
		}

		@Override
		public TreeStm visit(ArrayAssignment s) throws RuntimeException {
			
			TreeExp array = this.temps.get(s.id.id);
			TreeExp index = s.index.accept(this);
			TreeExp assignValue = s.rhs.accept(this);

			Label assignLabel = new Label();
			
			TreeStm boundsCheckStm = new TreeStmCJUMP(
					Rel.GE,
					index,
					new TreeExpMEM(array),
					raiseLabel,
					assignLabel);
			
			raises = true;

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
				new TreeStmLABEL(assignLabel),
				assignStm
			);
		}
	}
}
