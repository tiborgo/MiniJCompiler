package minijava.translate.visitors;

import minijava.translate.tree.TreeStmCJUMP;
import minijava.translate.tree.TreeStmEXP;
import minijava.translate.tree.TreeStmJUMP;
import minijava.translate.tree.TreeStmLABEL;
import minijava.translate.tree.TreeStmMOVE;
import minijava.translate.tree.TreeStmSEQ;

public interface TreeStmVisitor<A, T extends Throwable> {

  A visit(TreeStmMOVE stmMOVE);

  A visit(TreeStmEXP stmEXP);

  A visit(TreeStmJUMP stmJUMP);

  A visit(TreeStmCJUMP stmCJUMP);

  A visit(TreeStmSEQ stmSEQ);

  A visit(TreeStmLABEL stmLABEL);
}
