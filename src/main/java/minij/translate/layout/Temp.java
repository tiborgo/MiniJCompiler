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
package minij.translate.layout;

public class Temp implements Comparable<Temp> {

  private static int nextId = 500;

  private final int id;

  public Temp() {
    this.id = nextId++;
  }

  public static void resetCounter() {
    nextId = 0;
  }

  @Override
  public String toString() {
    return "t" + id;
  }

  @Override
  public boolean equals(Object obj) {
    return (obj instanceof Temp && ((Temp) obj).id == id);
  }

  @Override
  public int hashCode() {
    return id;
  }

  @Override
  public int compareTo(Temp o) {
    int oid = o.id;
    return (id < oid ? -1 : (id == oid ? 0 : 1));
  }
}
