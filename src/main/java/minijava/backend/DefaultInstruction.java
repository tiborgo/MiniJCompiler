package minijava.backend;

import java.util.Collections;
import java.util.List;

import minijava.backend.i386.Operand;
import minijava.translate.Label;
import minijava.translate.Temp;
import minijava.util.Function;
import minijava.util.Pair;

/**
 * Represents an assembly instruction with an arbitrary number of operands.
 */
public class DefaultInstruction extends Instruction {

	public DefaultInstruction(Operand... operands) {
		super(operands);
	}

	@Override
	public List<Label> jumps() {
		// All instructions except calls/jumps have no jumps
		return Collections.emptyList();
	}

	@Override
	public boolean isFallThrough() {
		// All instructions except calls/jumps are fall-through instructions
		return true;
	}

	@Override
	public final Label isLabel() {
		// Labels are directives, not instructions
		return null;
	}

	@Override
	public List<Temp> def() {
		return Collections.emptyList();
	}

	@Override
	public Pair<Temp, Temp> isMoveBetweenTemps() {
		return null;
	}

	@Override
	public Assem rename(Function<Temp, Temp> sigma) {
		return null;
	}
}
