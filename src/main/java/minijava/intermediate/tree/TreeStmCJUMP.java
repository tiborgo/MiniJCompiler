package minijava.intermediate.tree;

import minijava.intermediate.Label;

public class TreeStmCJUMP extends TreeStm {

  public enum Rel {

    EQ("=="), NE("!="), LT("<"), GT(">"), LE("<="), GE(">="),
    ULT("<u"), ULE(">=u"), UGT(">u"), UGE(">=u");
    private final String pretty;

    private Rel(String pretty) {
      this.pretty = pretty;
    }

    public Rel neg() {
      switch (this) {
        case EQ:
          return NE;
        case NE:
          return EQ;
        case LT:
          return GE;
        case GT:
          return LE;
        case LE:
          return GT;
        case GE:
          return LT;
        case ULT:
          return UGE;
        case UGT:
          return ULE;
        case ULE:
          return UGT;
        case UGE:
          return ULT;
        default:
          assert(false);
          return EQ;
      }
    }

    @Override
    public String toString() {
      return pretty;
    }
  }
  public final Rel rel;
  public final TreeExp left, right;
  public final Label ltrue, lfalse;

  public TreeStmCJUMP(Rel rel, TreeExp left, TreeExp right, Label ltrue, Label lfalse) {
    if (rel == null || left == null || right == null || ltrue == null || lfalse == null) {
      throw new NullPointerException();
    }
    this.rel = rel;
    this.left = left;
    this.right = right;
    this.ltrue = ltrue;
    this.lfalse = lfalse;
  }

  public
  @Override
  <A> A accept(TreeStmVisitor<A> visitor) {
    return visitor.visit(this);
  }

  @Override
  public String toString() {
    return "CJUMP(" + rel + ", " + left + ", " + right +
            ", " + ltrue + ", " + lfalse + ")";
  }
}
