package minijava.util;

public class Pair<A, B> {

  public final A fst;
  public final B snd;

  public Pair(A first, B second) {
    this.fst = first;
    this.snd = second;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Pair<A, B> other = (Pair<A, B>) obj;
    if (this.fst != other.fst && (this.fst == null || !this.fst.equals(other.fst))) {
      return false;
    }
    if (this.snd != other.snd && (this.snd == null || !this.snd.equals(other.snd))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 97 * hash + (this.fst != null ? this.fst.hashCode() : 0);
    hash = 97 * hash + (this.snd != null ? this.snd.hashCode() : 0);
    return hash;
  }

  @Override
  public String toString() {
    return "<" + fst + ", " + snd + ">";
  }
}
