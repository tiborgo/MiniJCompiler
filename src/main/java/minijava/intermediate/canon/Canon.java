package minijava.intermediate.canon;

import minijava.intermediate.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import minijava.intermediate.tree.*;
import minijava.util.Pair;

public class Canon implements FragmentVisitor<TreeStm, Fragment<List<TreeStm>>> {

  @Override
  public Fragment<List<TreeStm>> visit(FragmentProc<TreeStm> fragProc) {

    List<TreeStm> canonBody = new LinkedList<TreeStm>();
    canonBody.addAll(fragProc.body.accept(new CanonStm()));
    return new FragmentProc<List<TreeStm>>(fragProc.frame, canonBody);
  }

  /**
   * Given inputs (s, e) and t, this function returns a
   * pair (u,e') such that u has the same side effects
   * as s;t and after running u the expression e' has the
   * same value as the expression e has after evaluating s.
   */
  private static Pair<List<TreeStm>, TreeExp> compose(
          Pair<List<TreeStm>, TreeExp> c,
          List<TreeStm> stms) {
    if (stms.isEmpty()) {
      return c;
    }

    if (commute(stms, c.snd)) {
      List<TreeStm> newstms = new LinkedList<TreeStm>();
      newstms.addAll(c.fst);
      newstms.addAll(stms);
      return new Pair<List<TreeStm>, TreeExp>(newstms, c.snd);
    }
    TreeExp t = new TreeExpTEMP(new Temp());
    List<TreeStm> newstms = new LinkedList<TreeStm>();
    newstms.addAll(c.fst);
    newstms.add(new TreeStmMOVE(t, c.snd));
    newstms.addAll(stms);
    return new Pair<List<TreeStm>, TreeExp>(newstms, t);
  }

  /**
   * Returns true if s and e commute, i.e. if the execution of s
   * does not change the value of e.
   */
  private static boolean commute(TreeStm s, TreeExp e) {
    return (e instanceof TreeExpNAME) || (e instanceof TreeExpCONST) ||
            ((s instanceof TreeStmEXP) && (((TreeStmEXP) s).exp instanceof TreeExpCONST));
  }

  private static boolean commute(List<TreeStm> stms, TreeExp e) {
    for (TreeStm s : stms) {
      if (!commute(s, e)) {
        return false;
      }
    }
    return true;
  }

  // The following two functions are convenience functions that
  // are implemented in terms of compose above.
  static <A> Pair<List<TreeStm>, Pair<TreeExp, A>> compose(
          Pair<List<TreeStm>, TreeExp> c1,
          Pair<List<TreeStm>, A> c2) {

    Pair<List<TreeStm>, TreeExp> c1aug = compose(c1, c2.fst);
    return new Pair<List<TreeStm>, Pair<TreeExp, A>>(c1aug.fst, new Pair<TreeExp, A>(c1aug.snd, c2.snd));
  }

  static Pair<List<TreeStm>, List<TreeExp>> compose(
          List<Pair<List<TreeStm>, TreeExp>> clist) {

    List<Pair<List<TreeStm>, TreeExp>> clistRev = new LinkedList<Pair<List<TreeStm>, TreeExp>>(clist);
    Collections.reverse(clistRev);

    LinkedList<TreeExp> joined = new LinkedList<TreeExp>();
    List<TreeStm> stms = new LinkedList<TreeStm>();
    for (Pair<List<TreeStm>, TreeExp> c : clistRev) {
      Pair<List<TreeStm>, TreeExp> ca = compose(c, stms);
      stms = ca.fst;
      joined.addFirst(ca.snd);
    }
    return new Pair<List<TreeStm>, List<TreeExp>>(stms, joined);
  }
}
