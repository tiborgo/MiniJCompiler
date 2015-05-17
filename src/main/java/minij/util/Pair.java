/*
 * MiniJCompiler: Compiler for a subset of the Java(R) language
 *
 * (C) Copyright 2014-2015 Tibor Goldschwendt <goldschwendt@cip.ifi.lmu.de>,
 * Michael Seifert <mseifert@error-reports.org>
 *
 * This file is part of MiniJCompiler.
 *
 * MiniJCompiler is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MiniJCompiler is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MiniJCompiler.  If not, see <http://www.gnu.org/licenses/>.
 */
package minij.util;

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
    @SuppressWarnings("unchecked")
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
