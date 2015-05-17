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
package minij.canonicalize.visitors;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import minij.translate.layout.Fragment;
import minij.translate.layout.FragmentProc;
import minij.translate.layout.FragmentVisitor;
import minij.translate.layout.Temp;
import minij.translate.tree.TreeExp;
import minij.translate.tree.TreeExpCONST;
import minij.translate.tree.TreeExpNAME;
import minij.translate.tree.TreeExpTEMP;
import minij.translate.tree.TreeStm;
import minij.translate.tree.TreeStmEXP;
import minij.translate.tree.TreeStmMOVE;
import minij.util.Pair;

public class CanonVisitor implements FragmentVisitor<TreeStm, Fragment<List<TreeStm>>> {

  @Override
  public Fragment<List<TreeStm>> visit(FragmentProc<TreeStm> fragProc) {

    List<TreeStm> canonBody = new LinkedList<TreeStm>();
    canonBody.addAll(fragProc.body.accept(new CanonStmVisitor()));
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
