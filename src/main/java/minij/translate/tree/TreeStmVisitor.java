package minij.translate.tree;


public interface TreeStmVisitor<A, T extends Throwable> {

  A visit(TreeStmMOVE stmMOVE);

  A visit(TreeStmEXP stmEXP);

  A visit(TreeStmJUMP stmJUMP);

  A visit(TreeStmCJUMP stmCJUMP);

  A visit(TreeStmSEQ stmSEQ);

  A visit(TreeStmLABEL stmLABEL);
}
