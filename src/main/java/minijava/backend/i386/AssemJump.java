package minijava.backend.i386;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import minijava.backend.Assem;
import minijava.intermediate.Label;
import minijava.intermediate.Temp;
import minijava.util.Function;
import minijava.util.Pair;

public final class AssemJump implements Assem {

  public enum Kind {

    JMP, J, CALL
  }

  public enum Cond {

    E, NE, L, LE, G, GE, Z
  }
  private final Kind kind;
  private final Label label;
  private final Operand dest;
  private final Cond cond;

  public AssemJump(Kind kind, Label label) {
    this(kind, label, null, null);
  }

  public AssemJump(Kind kind, Operand dest) {
    this(kind, null, dest, null);
  }

  public AssemJump(Kind kind, Label label, Cond cond) {
    this(kind, label, null, cond);
  }

  public AssemJump(Kind kind, Label label, Operand dest, Cond cond) {
    assert (kind != Kind.J || cond != null) : "J needs condition argument";
    assert (kind == Kind.CALL || label != null) : "J and JMP need label as destination";
    assert (dest == null || dest instanceof Operand.Reg) : "dynamic destination of CALL must be Reg";
    this.kind = kind;
    this.label = label;
    this.dest = dest;
    this.cond = cond;
  }

  public List<Temp> use() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public List<Temp> def() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public List<Label> jumps() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public boolean isFallThrough() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public Pair<Temp, Temp> isMoveBetweenTemps() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public Label isLabel() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public String toString() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public Assem rename(Function<Temp, Temp> sigma) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
