package minijava.ast.visitors.baseblocks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

import minijava.intermediate.Label;
import minijava.intermediate.tree.TreeExpNAME;
import minijava.intermediate.tree.TreeStm;
import minijava.intermediate.tree.TreeStmJUMP;
import minijava.intermediate.tree.TreeStmLABEL;
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
