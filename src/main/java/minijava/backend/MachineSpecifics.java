package minijava.backend;

import java.util.List;
import minijava.intermediate.*;
import minijava.intermediate.tree.TreeStm;

public interface MachineSpecifics {

  /**
   * Machine word size, typically 4 on a 32 bit machine and 8 on a 64 bit machine.
   */
  int getWordSize();

  /**
   * Returns an array of all machine registers. 
   * <p>
   * May return {@code null} if the machine does not have registers. 
   * In this case, an unlimited number of temporaries will be used.
   */
  Temp[] getAllRegisters();

  /**
   * Returns an array of all general purpose registers that may be 
   * used without restriction for register allocation.
   * <p>
   * May return {@code null} if the machine does not have registers. 
   * In this case, an unlimited number of temporaries will be used.
   */
  Temp[] getGeneralPurposeRegisters();

  /**
   * Construct a new procedure frame with name {@code name} and
   * {@paramCount} parameters.
   */
  Frame newFrame(Label name, int paramCount);

  /** Spilling method for the register allocator.
   */
  List<Assem> spill(Frame frame, List<Assem> instrs, List<Temp> toSpill);

  /**
   * Method for compiling whole fragments. 
   * 
   * This method corresponds roughly to procEntryExit2 in the book.
   */
  Fragment<List<Assem>> codeGen(Fragment<List<TreeStm>> frag);

  /**
   * Method for converting a list of fragments into actual
   * assembly code.
   */
  String printAssembly(List<Fragment<List<Assem>>> frags);
}
