package minijava.canonicalize;

import java.util.ArrayList;
import java.util.List;

import minijava.Configuration;
import minijava.Logger;
import minijava.canonicalize.visitors.CanonVisitor;
import minijava.translate.baseblocks.BaseBlock;
import minijava.translate.baseblocks.Generator;
import minijava.translate.baseblocks.ToTreeStmConverter;
import minijava.translate.baseblocks.Tracer;
import minijava.translate.layout.FragmentProc;
import minijava.translate.tree.TreeStm;
import minijava.translate.visitors.IntermediatePrettyPrintVisitor;

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
