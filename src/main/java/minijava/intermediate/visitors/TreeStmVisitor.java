package minijava.intermediate.visitors;

import minijava.intermediate.tree.TreeStmCJUMP;
import minijava.intermediate.tree.TreeStmEXP;
import minijava.intermediate.tree.TreeStmJUMP;
import minijava.intermediate.tree.TreeStmLABEL;
import minijava.intermediate.tree.TreeStmMOVE;
import minijava.intermediate.tree.TreeStmSEQ;

public interface TreeStmVisitor<A, T extends Throwable> {

  A visit(TreeStmMOVE stmMOVE);

  A visit(TreeStmEXP stmEXP);

  A visit(TreeStmJUMP stmJUMP);

  A visit(TreeStmCJUMP stmCJUMP);

  A visit(TreeStmSEQ stmSEQ);

  A visit(TreeStmLABEL stmLABEL);
}
