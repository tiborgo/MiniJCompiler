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
package minij.ast.visitors.baseblocks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import minij.translate.baseblocks.Generator;
import minij.translate.layout.Label;
import minij.translate.tree.TreeExpNAME;
import minij.translate.tree.TreeStm;
import minij.translate.tree.TreeStmJUMP;
import minij.translate.tree.TreeStmLABEL;

import org.junit.Test;

public class GeneratorTest {

	@Test
	public void testGenerateLabelWithinBlock() throws Exception {
		List<TreeStm> statements = new ArrayList<>();
		// Method start
		Label methodLabel = new Label();
		statements.add(new TreeStmLABEL(methodLabel));
		// Some label
		statements.add(new TreeStmLABEL(new Label()));
		// Jump to start
		statements.add(new TreeStmJUMP(new TreeExpNAME(methodLabel), Collections.singletonList(methodLabel)));
		Generator.BaseBlockContainer baseBlockContainer = Generator.generate(statements);
		assertEquals(2, baseBlockContainer.baseBlocks.size());
	}
}
