package minijava.intermediate.canon;

import java.util.LinkedList;
import java.util.List;

import minijava.intermediate.Temp;
import minijava.intermediate.tree.TreeExp;
import minijava.intermediate.tree.TreeExpCALL;
import minijava.intermediate.tree.TreeExpCONST;
import minijava.intermediate.tree.TreeExpESEQ;
import minijava.intermediate.tree.TreeExpMEM;
import minijava.intermediate.tree.TreeExpNAME;
import minijava.intermediate.tree.TreeExpOP;
import minijava.intermediate.tree.TreeExpTEMP;
import minijava.intermediate.tree.TreeStm;
import minijava.intermediate.tree.TreeStmMOVE;
import minijava.intermediate.visitors.TreeExpVisitor;
import minijava.util.Pair;

public class CanonExp implements TreeExpVisitor<Pair<List<TreeStm>, TreeExp>, RuntimeException> {

  // canonicalize e and remove toplevel call
  Pair<List<TreeStm>, TreeExp> canonNoTopCALL(TreeExp e) {

    Pair<List<TreeStm>, TreeExp> ce = e.accept(this);
    if (ce.snd instanceof TreeExpCALL) {
      TreeExp call = (TreeExpCALL) ce.snd;
      TreeExp t = new TreeExpTEMP(new Temp());
      List<TreeStm> stms = new LinkedList<TreeStm>(ce.fst);
      stms.add(new TreeStmMOVE(t, call));
      ce = new Pair<List<TreeStm>, TreeExp>(stms, t);
    }
    return ce;
  }

  @Override
  public Pair<List<TreeStm>, TreeExp> visit(TreeExpCONST expCONST) {
    return new Pair<List<TreeStm>, TreeExp>(new LinkedList<TreeStm>(), expCONST);
  }

  @Override
  public Pair<List<TreeStm>, TreeExp> visit(TreeExpNAME expNAME) {
    return new Pair<List<TreeStm>, TreeExp>(new LinkedList<TreeStm>(), expNAME);
  }

  @Override
  public Pair<List<TreeStm>, TreeExp> visit(TreeExpTEMP expTEMP) {
    return new Pair<List<TreeStm>, TreeExp>(new LinkedList<TreeStm>(), expTEMP);
  }

  @Override
  public Pair<List<TreeStm>, TreeExp> visit(TreeExpMEM expMEM) {
    Pair<List<TreeStm>, TreeExp> caddr = canonNoTopCALL(expMEM.addr);
    return new Pair<List<TreeStm>, TreeExp>(caddr.fst, new TreeExpMEM(caddr.snd));
  }

  @Override
  public Pair<List<TreeStm>, TreeExp> visit(TreeExpOP expOP) {
    Pair<List<TreeStm>, TreeExp> cleft = canonNoTopCALL(expOP.left);
    Pair<List<TreeStm>, TreeExp> cright = canonNoTopCALL(expOP.right);
    Pair<List<TreeStm>, Pair<TreeExp, TreeExp>> c = Canon.compose(cleft, cright);
    return new Pair<List<TreeStm>, TreeExp>(c.fst, new TreeExpOP(expOP.op, c.snd.fst, c.snd.snd));
  }

  @Override
  public Pair<List<TreeStm>, TreeExp> visit(TreeExpCALL expCALL) {
    Pair<List<TreeStm>, TreeExp> cfunc = canonNoTopCALL(expCALL.func);

    // canonicalize arguments
    List<Pair<List<TreeStm>, TreeExp>> cargs = new LinkedList<Pair<List<TreeStm>, TreeExp>>();
    for (TreeExp arg : expCALL.args) {
      cargs.add(canonNoTopCALL(arg));
    }

    Pair<List<TreeStm>, List<TreeExp>> joinedArgs = Canon.compose(cargs);
    Pair<List<TreeStm>, Pair<TreeExp, List<TreeExp>>> joinedFuncArgs =
            Canon.compose(cfunc, joinedArgs);

    return new Pair<List<TreeStm>, TreeExp>(joinedFuncArgs.fst,
            new TreeExpCALL(joinedFuncArgs.snd.fst, joinedFuncArgs.snd.snd));
  }

  @Override
  public Pair<List<TreeStm>, TreeExp> visit(TreeExpESEQ expESEQ) {
    List<TreeStm> cstm = expESEQ.stm.accept(new CanonStm());
    Pair<List<TreeStm>, TreeExp> cres = canonNoTopCALL(expESEQ.res);
    List<TreeStm> stms = new LinkedList<TreeStm>(cstm);
    stms.addAll(cres.fst);
    return new Pair<List<TreeStm>, TreeExp>(stms, cres.snd);
  }
}
