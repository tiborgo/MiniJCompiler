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
package minij.instructionselection;

import java.util.List;

import minij.instructionselection.assems.Assem;
import minij.translate.layout.Fragment;
import minij.translate.layout.Frame;
import minij.translate.layout.Label;
import minij.translate.layout.Temp;
import minij.translate.tree.TreeStm;

/**
 * Provides information about the target architecture of the compiler backend.
 */
public interface MachineSpecifics {

  /**
   * Machine word size, typically 4 on a 32 bit machine and 8 on a 64 bit machine.
   */
  int getWordSize();

  /**
   * Returns an array of all machine registers. 
   * <p>
   * May return {@code null} if the machine does not have registers. 
   * In this case, an unlimited number of temporaries will be used.
   */
  Temp[] getAllRegisters();

  /**
   * Returns an array of all general purpose registers that may be 
   * used without restriction for register allocation.
   * <p>
   * May return {@code null} if the machine does not have registers. 
   * In this case, an unlimited number of temporaries will be used.
   */
  Temp[] getGeneralPurposeRegisters();

  /**
   * Construct a new procedure frame with name {@code name} and
   * {@paramCount} parameters.
   */
  Frame newFrame(Label name, int paramCount);

  /** Spilling method for the register allocator.
   */
  List<Assem> spill(Frame frame, List<Assem> instrs, List<Temp> toSpill);

  /**
   * Method for compiling whole fragments. 
   * 
   * This method corresponds roughly to procEntryExit2 in the book.
   */
  Fragment<List<Assem>> codeGen(Fragment<List<TreeStm>> frag);

  /**
   * Method for converting a list of fragments into actual
   * assembly code.
   */
  String printAssembly(List<Fragment<List<Assem>>> frags);
}
