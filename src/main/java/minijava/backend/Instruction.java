package minijava.backend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import minijava.backend.i386.Operand;
import minijava.intermediate.Temp;

/**
 * Represents an assembly instruction with an arbitrary number of operands.
 */
public abstract class Instruction implements Assem {
	public final List<Operand> operands;

	public Instruction(Operand... operands) {
		this.operands = new ArrayList<>(Arrays.asList(operands));
	}

	@Override
	public List<Temp> use() {
		ArrayList<Temp> usedTemporaries = new ArrayList<>();
		for (Operand operand : operands) {
			if (operand instanceof Operand.Reg) {
				usedTemporaries.add(((Operand.Reg) operand).reg);
			} else if (operand instanceof Operand.Mem) {
				Operand.Mem memoryAccess = (Operand.Mem) operand;
				if (memoryAccess.base != null) {
					usedTemporaries.add(memoryAccess.base);
				}
				if (memoryAccess.index != null) {
					usedTemporaries.add(memoryAccess.index);
				}
			}
		}
		return usedTemporaries;
	}
}
