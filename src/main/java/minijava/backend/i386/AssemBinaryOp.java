package minijava.backend.i386;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import minijava.backend.Assem;
import minijava.intermediate.Label;
import minijava.intermediate.Temp;
import minijava.util.Function;
import minijava.util.Pair;

final class AssemBinaryOp implements Assem {

  enum Kind {

    MOV, ADD, SUB, SHL, SHR, SAL, SAR, AND, OR, XOR, TEST, CMP, LEA
  }
  private final Operand src;
  private final Operand dst;
  private final Kind kind;

  AssemBinaryOp(Kind kind, Operand dst, Operand src) {
    assert (kind != null && src != null && dst != null);
    assert (!((src instanceof Operand.Mem) && (dst instanceof Operand.Mem)));
    assert (kind != Kind.LEA || ((src instanceof Operand.Mem) && (dst instanceof Operand.Reg)));
    this.src = src;
    this.dst = dst;
    this.kind = kind;
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
