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
package minij.translate.tree;


public interface TreeExpVisitor<A, T extends Throwable> {

	public A visit(TreeExpCALL e) throws T;

	public A visit(TreeExpCONST e) throws T;

	public A visit(TreeExpESEQ e) throws T;

	public A visit(TreeExpMEM e) throws T;

	public A visit(TreeExpNAME e) throws T;

	public A visit(TreeExpOP e) throws T;

	public A visit(TreeExpTEMP e) throws T;
}
