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
package minij.backend.i386.assems;

import minij.instructionselection.assems.Directive;
import minij.instructionselection.assems.Instruction;


public interface AssemVisitor<A, T extends Throwable> {
	A visit(AssemBinaryOp assem) throws T;
	A visit(AssemInstr assem) throws T;
	A visit(AssemJump assem) throws T;
	A visit(AssemLabel assem) throws T;
	A visit(AssemUnaryOp assem) throws T;
	A visit(Directive assem) throws T;
	A visit(Instruction instruction) throws T;
}
