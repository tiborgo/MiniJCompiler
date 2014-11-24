package minijava.backend.i386;

import java.util.Collections;
import java.util.List;
import minijava.backend.Assem;
import minijava.intermediate.Label;
import minijava.intermediate.Temp;
import minijava.util.Function;
import minijava.util.Pair;

final class AssemLabel implements Assem {

  private final Label label;

  AssemLabel(Label label) {
    this.label = label;
  }

  public List<Temp> use() {
    return Collections.emptyList();
  }

  public List<Temp> def() {
    return Collections.emptyList();
  }

  public List<Label> jumps() {
    return Collections.emptyList();
  }

  public boolean isFallThrough() {
    return true;
  }

  public Pair<Temp, Temp> isMoveBetweenTemps() {
    return null;
  }

  public Label isLabel() {
    return label;
  }

  public String toString() {
    return label + ":\n";
  }

  public Assem rename(Function<Temp, Temp> sigma) {
    return this;
  }
}
