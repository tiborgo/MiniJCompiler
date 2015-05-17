package minij.translate.tree;


public class TreeExpESEQ extends TreeExp {
  public final TreeStm stm;
  public final TreeExp res;

  public TreeExpESEQ(TreeStm stm, TreeExp res) {
    if (stm == null || res == null) {
      throw new NullPointerException();
    }
    this.stm = stm;
    this.res = res;
  }

  @Override
  public <A, T extends Throwable> A accept(TreeExpVisitor<A, T> visitor) throws T {
    return visitor.visit(this);
  }

  @Override
  public String toString() {
    return "ESEQ(" + stm + ", " + res + ")";
  }

}
