package minijava.intermediate.tree;

public interface TreeStmVisitor<A> {

  A visit(TreeStmMOVE stmMOVE);

  A visit(TreeStmEXP stmEXP);

  A visit(TreeStmJUMP stmJUMP);

  A visit(TreeStmCJUMP stmCJUMP);

  A visit(TreeStmSEQ stmSEQ);

  A visit(TreeStmLABEL stmLABEL);
}
