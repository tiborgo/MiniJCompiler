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
package minij.canonicalize;

import java.util.ArrayList;
import java.util.List;

import minij.Configuration;
import minij.Logger;
import minij.canonicalize.visitors.CanonVisitor;
import minij.translate.baseblocks.BaseBlock;
import minij.translate.baseblocks.Generator;
import minij.translate.baseblocks.ToTreeStmConverter;
import minij.translate.baseblocks.Tracer;
import minij.translate.layout.FragmentProc;
import minij.translate.tree.TreeStm;
import minij.translate.visitors.IntermediatePrettyPrintVisitor;

public class Canonicalizer {

	public static List<FragmentProc<List<TreeStm>>> canonicalize(Configuration config, List<FragmentProc<TreeStm>> intermediate) throws CanonicalizerException {

		try {
			StringBuilder outputBuilder = new StringBuilder();
			
			List<FragmentProc<List<TreeStm>>> intermediateCanonicalized = new ArrayList<>(intermediate.size());
			
			
			
			for (FragmentProc<TreeStm> fragment : intermediate) {
				FragmentProc<List<TreeStm>> canonFrag = (FragmentProc<List<TreeStm>>) fragment.accept(new CanonVisitor());

				if (config.printCanonicalizedIntermediate) {
					outputBuilder
						.append("*******")
						.append(System.lineSeparator());
					for (TreeStm stm : canonFrag.body) {
						outputBuilder
							.append(stm.accept(new IntermediatePrettyPrintVisitor()))
							.append(System.lineSeparator())
							.append("-----")
							.append(System.lineSeparator());
					}
				}

				Generator.BaseBlockContainer baseBlocks = Generator.generate(canonFrag.body);
				List<BaseBlock> tracedBaseBlocks = Tracer.trace(baseBlocks);
				List<TreeStm> tracedBody = ToTreeStmConverter.convert(tracedBaseBlocks, baseBlocks.startLabel, baseBlocks.endLabel);

				intermediateCanonicalized.add(new FragmentProc<List<TreeStm>>(canonFrag.frame, tracedBody));
			}

			Logger.logVerbosely("Successfully canonicalized intermediate language");
			
			if (config.printCanonicalizedIntermediate) {
				Logger.log(outputBuilder.toString());
			}

			return intermediateCanonicalized;
		}
		catch (Exception e) {
			throw new CanonicalizerException("Failed to canonicalize intermediate language", e);
		}
	}

}
