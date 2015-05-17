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

public final class Label {

  public static boolean leadingUnderscore;
  private static int nextId = 0;
  private final String name;  // invariant: is always non-null

  /**
   * Generates a fresh label.
   */
  public Label() {
    name = "L$$" + nextId++;
  }

  /**
   * Generates a label with the given name.
   *
   * The parameter {@code name} must not be null.
   * Names starting with {@code $$} are reserved and may not be used.
   *
   * @param name Name of label
   */
  public Label(String name) {
    if (name == null) {
      throw new NullPointerException();
    }
    if (name.startsWith("$$")) {
      throw new RuntimeException("Label name " + name + " is reserved and cannot be used");
    }
    this.name = name;
  }

  public static void resetCounter() {
    nextId = 0;
  }

  @Override
  public String toString() {
	if (leadingUnderscore) {
		return "_" + name;
	}
    return name;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Label other = (Label) obj;
    if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }
}
