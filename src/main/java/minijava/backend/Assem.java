package minijava.backend;

import java.util.List;
import minijava.intermediate.Label;
import minijava.intermediate.Temp;
import minijava.util.Function;
import minijava.util.Pair;

// Assumption: is immutable!
public interface Assem {

  public List<Temp> use();

  public List<Temp> def();

  public List<Label> jumps();

  public boolean isFallThrough();

  public Pair<Temp, Temp> isMoveBetweenTemps();

  public Label isLabel();

  public Assem rename(Function<Temp, Temp> sigma);
}
