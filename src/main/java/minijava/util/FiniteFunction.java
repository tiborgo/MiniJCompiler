package minijava.util;

import java.util.Map;

public class FiniteFunction<A> implements Function<A, A> {

  private final Map<A, A> map;

  public FiniteFunction(Map<A, A> map) {
    this.map = map;
  }

  @Override
  public A apply(A a) {
    A b = map.get(a);
    if (b != null) {
      return b;
    } else {
      return a;
    }
  }
}
