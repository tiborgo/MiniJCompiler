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
package minij.parse.rules.types;

public class Integer extends Type {

  public Integer() {
  }

  @Override
  public String toString() {
    return "int";
  }

  @Override
  public <A, T extends Throwable> A accept(TypeVisitor<A, T> v) throws T {
    return v.visit(this);
  }

  @Override
  public boolean equals(Object obj) {
    return (obj instanceof Integer);
  }

  @Override
  public int hashCode() {
    int hash = 5;
    return hash;
  }
}