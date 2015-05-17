package minij.translate.tree;

import java.util.List;

import minij.translate.layout.Label;

public abstract class TreeStm {

  public abstract <A, T extends Throwable> A accept(TreeStmVisitor<A, T> visitor)  throws T;

  // construct single TreeStm from a sequence of TreeStms
  public static TreeStm fromArray(TreeStm... stms) {
    TreeStm s = null;
    for (int i = stms.length - 1; i >= 0; i--) {
      s = (s == null) ? stms[i] : new TreeStmSEQ(stms[i], s);
    }
    // no statement?
    if (s == null) {
      s = TreeStm.getNOP();
    }
    return s;
  }

  public static TreeStm fromList(List<TreeStm> stms) {
    TreeStm[] stmArray = new TreeStm[stms.size()];
    stms.toArray(stmArray);
    return TreeStm.fromArray(stmArray);
  }

  public static TreeStm getNOP() {
    return new TreeStmLABEL(new Label());
  }
  
}
