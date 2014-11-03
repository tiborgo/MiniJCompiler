package minijava.intermediate.tree;

public interface TreeExpVisitor<A> {

  A visit(TreeExpCONST expCONST);

  A visit(TreeExpNAME expNAME);

  A visit(TreeExpTEMP expTEMP);

  A visit(TreeExpMEM expMEM);

  A visit(TreeExpOP expOP);

  A visit(TreeExpCALL expCALL);

  A visit(TreeExpESEQ expESEQ);
}
