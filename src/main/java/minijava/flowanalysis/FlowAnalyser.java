package minijava.flowanalysis;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import minijava.Configuration;
import minijava.Logger;
import minijava.backend.i386.visitors.I386PrintAssemblyVisitor;
import minijava.instructionselection.assems.Assem;
import minijava.translate.layout.FragmentProc;
import minijava.translate.layout.Temp;
import minijava.util.SimpleGraph;

import org.apache.commons.lang3.StringUtils;

public class FlowAnalyser {

	public static SimpleGraph<Temp> analyseFlow(Configuration config, FragmentProc<List<Assem>> assemFragment) throws FlowAnalyserException {
		
		try {
		
			SimpleGraph<Assem> controlFlowGraph = ControlFlowGraphBuilder.build(assemFragment);
			Map<Assem, LivenessSetsBuilder.InOut> inOut = LivenessSetsBuilder.build(controlFlowGraph, assemFragment.body);
			SimpleGraph<Temp> interferenceGraph = InterferenceGraphBuilder.build(controlFlowGraph, inOut);
			
			
			int maxInterference = 0;
			I386PrintAssemblyVisitor visitor = new I386PrintAssemblyVisitor();
			Iterator<Assem> iter = assemFragment.body.iterator();
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
