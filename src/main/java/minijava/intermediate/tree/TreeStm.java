package minijava.intermediate.tree;

import java.util.List;
import minijava.intermediate.Label;

public abstract class TreeStm {

  public abstract <A> A accept(TreeStmVisitor<A> visitor);

  // construct single TreeStm from a sequence of TreeStms
  public static TreeStm fromArray(TreeStm[] stms) {
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
