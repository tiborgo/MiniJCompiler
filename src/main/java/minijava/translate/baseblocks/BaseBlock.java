package minijava.translate.baseblocks;

import java.util.ArrayList;
import java.util.List;

import minijava.translate.layout.Label;
import minijava.translate.tree.TreeExpNAME;
import minijava.translate.tree.TreeStm;
import minijava.translate.tree.TreeStmCJUMP;
import minijava.translate.tree.TreeStmJUMP;
import minijava.translate.tree.TreeStmLABEL;

/**
 * Represents a snippet of low-level code that is used for the serialization of the call graph.
 * @see minijava.translate.baseblocks.Tracer
 */
public class BaseBlock {
	/** Entry point of the code block. */
	public final Label         label;
	/** Statements to be executed. */
	public final List<TreeStm> body;
	/** Jump statement when exiting the block. */
	public final TreeStm       jump;

	public BaseBlock(List<TreeStm> body) {
		
		if (body.size() < 2) {
			throw new IllegalArgumentException("A BaseBlock must consist of at least two statements.");
		}
		if (!(body.get(0) instanceof TreeStmLABEL)) {
			throw new IllegalArgumentException("A BaseBlock must start with a label.");
		}
		if (!(body.get(body.size()-1) instanceof TreeStmCJUMP || body.get(body.size()-1) instanceof TreeStmJUMP)) {
			throw new IllegalArgumentException("A BaseBlock must wnd with a jump or conditional jump.");
		}
		
		this.body = new ArrayList<>(body);
		this.label = ((TreeStmLABEL)body.get(0)).label;
		this.jump = body.get(body.size()-1);
	}
	
	@Override
	public String toString() {
		return "{" + label + ",...," +
				((jump instanceof TreeStmJUMP) ?
						"(JUMP, " + ((TreeExpNAME)((TreeStmJUMP)jump).dest).label + ")" :
							"(CJUMP,..., " + ((TreeStmCJUMP)jump).ltrue + ", " + ((TreeStmCJUMP)jump).lfalse + ")") +
				"}";
	}
	
}
