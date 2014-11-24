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

final class AssemInstr implements Assem {

  enum Kind {

    RET, LEAVE, NOP
  }
  private final Kind kind;

  AssemInstr(Kind kind) {
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
