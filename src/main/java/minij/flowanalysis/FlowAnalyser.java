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
package minij.flowanalysis;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import minij.Configuration;
import minij.Logger;
import minij.backend.i386.visitors.I386PrintAssemblyVisitor;
import minij.instructionselection.assems.Assem;
import minij.util.SimpleGraph;

import org.apache.commons.lang3.StringUtils;

public class FlowAnalyser {

	public static SimpleGraph<CoalesceableTemp> analyseFlow(Configuration config, List<Assem> body, String methodName) throws FlowAnalyserException {
		
		try {
		
			SimpleGraph<Assem> controlFlowGraph = ControlFlowGraphBuilder.build(body, methodName);
			Map<Assem, LivenessSetsBuilder.InOut> inOut = LivenessSetsBuilder.build(controlFlowGraph, body);
			SimpleGraph<CoalesceableTemp> interferenceGraph = InterferenceGraphBuilder.build(controlFlowGraph, inOut);
			
			if (config.printInterferenceGraphs) {
				Logger.log(interferenceGraph.getDot());
			}
			
			int maxInterference = 0;
			I386PrintAssemblyVisitor visitor = new I386PrintAssemblyVisitor();
			Iterator<Assem> iter = body.iterator();
			StringBuilder inOutStringBuilder = new StringBuilder();
	
			if (config.printFlowAnalysisDetails) {
				while (iter.hasNext()) {
		
					Assem next = iter.next();
					String nextString = StringUtils.rightPad(next.accept(visitor), 30);
					String padding = StringUtils.rightPad("", 30);
					String separator = StringUtils.rightPad("", 100, '-');
		
					LivenessSetsBuilder.InOut inOutN = inOut.get(next);
					if (inOutN.in.size() > maxInterference) {
						maxInterference = inOutN.in.size();
					}
					if (inOutN.out.size() > maxInterference) {
						maxInterference = inOutN.out.size();
					}
		
					inOutStringBuilder
						.append(nextString)
						.append("   in: ")
						.append(inOutN.in)
						.append(System.lineSeparator())
						.append(padding)
						.append("   out:")
						.append(inOutN.out)
						.append(System.lineSeparator())
						.append(separator)
						.append(System.lineSeparator());
				}
				inOutStringBuilder
					.append("Max interference: ")
					.append(maxInterference);
				
				Logger.log(inOutStringBuilder.toString());
			}
			
			return interferenceGraph;
		}
		catch (Exception e) {
			throw new FlowAnalyserException("Failed to analyse control flow", e);
		}
	}

}
