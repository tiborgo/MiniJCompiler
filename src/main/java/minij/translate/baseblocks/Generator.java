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
package minij.translate.baseblocks;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import minij.translate.layout.Label;
import minij.translate.tree.TreeStm;
import minij.translate.tree.TreeStmCJUMP;
import minij.translate.tree.TreeStmJUMP;
import minij.translate.tree.TreeStmLABEL;

public class Generator {

	/**
	 * Class that groups a number of code blocks while providing a common entry and a common exit point.
	 */
	public static class BaseBlockContainer {
		
		public final Map<Label, BaseBlock> baseBlocks;
		public final Label startLabel;
		public final Label endLabel;
		
		private BaseBlockContainer(Map<Label, BaseBlock> baseBlocks, Label startLabel, Label endLabel) {
			this.baseBlocks = baseBlocks;
			this.startLabel = startLabel;
			this.endLabel = endLabel;
		}
	}

	/**
	 * Creates {@code BaseBlock}s from the specified list of statements and returns them as a {@code BaseBlockContainer}
	 * with the specified exit point.
	 * @param stms Canonicalized list of statements.
	 * @param endLabel Common exit point.
	 * @return Object containing base blocks that were constructed from the statement list.
	 */
	public static BaseBlockContainer generate(List<TreeStm> stms) {
		
		Map<Label, BaseBlock> baseBlocks = new HashMap<>();
		
		List<TreeStm> currentBaseBlock = new LinkedList<>();
		Label currentBaseBlockLabel;
		int i = 1;
		
		Label startLabel;
		if (!(stms.get(0) instanceof TreeStmLABEL)) {
			startLabel = new Label();
			currentBaseBlockLabel = startLabel;
			currentBaseBlock.add(new TreeStmLABEL(currentBaseBlockLabel));
		}
		else {
			startLabel = ((TreeStmLABEL)stms.get(0)).label;
			currentBaseBlockLabel = startLabel;
			currentBaseBlock.add(stms.get(0));
			i++;
		}
		
		for (; i < stms.size(); i++) {
			
			while (i < stms.size() && !(stms.get(i-1) instanceof TreeStmLABEL)) {
				
				currentBaseBlock.add(stms.get(i-1));
				
				// new base block
				if (stms.get(i) instanceof TreeStmLABEL) {
					
					if (!(stms.get(i-1) instanceof TreeStmJUMP || 
							stms.get(i-1) instanceof TreeStmCJUMP)) {
						
						currentBaseBlock.add(TreeStmJUMP.jumpToLabel(((TreeStmLABEL)stms.get(i)).label));
					}
				}
				else if (!(stms.get(i) instanceof TreeStmLABEL) &&
						(stms.get(i-1) instanceof TreeStmJUMP ||
								stms.get(i-1) instanceof TreeStmCJUMP)) {
					// Dead code -> skip
					do {
						i++;
					}
					while(i < stms.size() && !(stms.get(i) instanceof TreeStmLABEL));
				}
				
				i++;
			}
			
			if (i == stms.size()) {
				break;
			}
			
			if (currentBaseBlock.size() == 1) {
				currentBaseBlock.add(TreeStmJUMP.jumpToLabel(((TreeStmLABEL)stms.get(i-1)).label));
			}
			
			baseBlocks.put(
				currentBaseBlockLabel,
				new BaseBlock(currentBaseBlock)
			);
			currentBaseBlock = new LinkedList<>();
			currentBaseBlockLabel = ((TreeStmLABEL)stms.get(i-1)).label;
			currentBaseBlock.add(stms.get(i-1));
		}
		
		currentBaseBlock.add(stms.get(stms.size()-1));

		Label endLabel = new Label();
		if (!((stms.get(stms.size()-1) instanceof TreeStmJUMP) ||
				(stms.get(stms.size()-1) instanceof TreeStmCJUMP))) {

			currentBaseBlock.add(TreeStmJUMP.jumpToLabel(endLabel));
		}
		
		baseBlocks.put(
			currentBaseBlockLabel,
			new BaseBlock(currentBaseBlock)
		);
		
		return new BaseBlockContainer(baseBlocks, startLabel, endLabel);
	}
}
