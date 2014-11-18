package minijava.intermediate.canon;

import java.util.LinkedList;
import java.util.List;

import minijava.intermediate.tree.TreeExp;
import minijava.intermediate.tree.TreeExpESEQ;
import minijava.intermediate.tree.TreeExpMEM;
import minijava.intermediate.tree.TreeExpTEMP;
import minijava.intermediate.tree.TreeStm;
import minijava.intermediate.tree.TreeStmCJUMP;
import minijava.intermediate.tree.TreeStmEXP;
import minijava.intermediate.tree.TreeStmJUMP;
import minijava.intermediate.tree.TreeStmLABEL;
import minijava.intermediate.tree.TreeStmMOVE;
import minijava.intermediate.tree.TreeStmSEQ;
import minijava.intermediate.visitors.TreeStmVisitor;
import minijava.util.Pair;

public class CanonStm implements TreeStmVisitor<List<TreeStm>, RuntimeException> {

  private List<TreeStm> canon(TreeStm s) {
    return s.accept(this);
  }

  // canonicalize expression e, without removing top-level call
  private Pair<List<TreeStm>, TreeExp> canon(TreeExp e) {
    return e.accept(new CanonExp());
  }

  // canonicalize expression e, removing top-level call
  private Pair<List<TreeStm>, TreeExp> canonNoTopCALL(TreeExp e) {
    CanonExp ce = new CanonExp();
    return ce.canonNoTopCALL(e);
  }

  @Override
  public List<TreeStm> visit(TreeStmMOVE stmMOVE) {
    if (stmMOVE.dest instanceof TreeExpMEM) {
      TreeExp addr = ((TreeExpMEM) stmMOVE.dest).addr;
      Pair<List<TreeStm>, TreeExp> caddr = canonNoTopCALL(addr);
      Pair<List<TreeStm>, TreeExp> csrc = canonNoTopCALL(stmMOVE.src);
      Pair<List<TreeStm>, Pair<TreeExp, TreeExp>> c = Canon.compose(caddr, csrc);
      List<TreeStm> stms = new LinkedList<TreeStm>();
      stms.addAll(c.fst);
      stms.add(new TreeStmMOVE(new TreeExpMEM(c.snd.fst), c.snd.snd));
      return stms;
    } else if (stmMOVE.dest instanceof TreeExpTEMP) {
      Pair<List<TreeStm>, TreeExp> csrc = canon(stmMOVE.src);
      List<TreeStm> stms = new LinkedList<TreeStm>();
      stms.addAll(csrc.fst);
      stms.add(new TreeStmMOVE(stmMOVE.dest, csrc.snd));
      return stms;
    } else if (stmMOVE.dest instanceof TreeExpESEQ) {
       TreeExpESEQ dst = (TreeExpESEQ) stmMOVE.dest;
       return new TreeStmSEQ(dst.stm, new TreeStmMOVE(dst.res, stmMOVE.src)).accept(this);   
    } else {
      throw new Error("Left-hand side of MOVE must be TEMP, MEM or ESEQ.");
    }
  }

  @Override
  public List<TreeStm> visit(TreeStmEXP stmEXP) {
    Pair<List<TreeStm>, TreeExp> cexp = canon(stmEXP.exp);
    List<TreeStm> stms = new LinkedList<TreeStm>(cexp.fst);
    stms.add(new TreeStmEXP(cexp.snd));
    return stms;
  }

  @Override
  public List<TreeStm> visit(TreeStmJUMP stmJUMP) {
    Pair<List<TreeStm>, TreeExp> cdest = canonNoTopCALL(stmJUMP.dest);
    List<TreeStm> stms = new LinkedList<TreeStm>(cdest.fst);
    stms.add(new TreeStmJUMP(cdest.snd, stmJUMP.poss));
    return stms;
  }

  @Override
  public List<TreeStm> visit(TreeStmCJUMP stmCJUMP) {
    Pair<List<TreeStm>, TreeExp> cleft = canonNoTopCALL(stmCJUMP.left);
    Pair<List<TreeStm>, TreeExp> cright = canonNoTopCALL(stmCJUMP.right);
    Pair<List<TreeStm>, Pair<TreeExp, TreeExp>> j = Canon.compose(cleft, cright);
    TreeExp jleft = j.snd.fst;
    TreeExp jright = j.snd.snd;
    List<TreeStm> stms = new LinkedList<TreeStm>(j.fst);
    stms.add(new TreeStmCJUMP(stmCJUMP.rel, jleft, jright, stmCJUMP.ltrue, stmCJUMP.lfalse));
    return stms;
  }

  @Override
  public List<TreeStm> visit(TreeStmSEQ stmSEQ) {
    List<TreeStm> cfirst = canon(stmSEQ.first);
    List<TreeStm> csecond = canon(stmSEQ.second);
    List<TreeStm> stms = new LinkedList<TreeStm>(cfirst);
    stms.addAll(csecond);
    return stms;
  }

  @Override
  public List<TreeStm> visit(TreeStmLABEL stmLABEL) {
    List<TreeStm> stms = new LinkedList<TreeStm>();
    stms.add(stmLABEL);
    return stms;
  }
}
