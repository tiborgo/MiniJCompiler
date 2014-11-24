package minijava.backend.i386;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import minijava.intermediate.Temp;
import minijava.util.Function;

abstract class Operand {

  final static class Imm extends Operand {

    public final int imm;

    public Imm(Integer imm) {
      assert (imm != null);
      this.imm = imm;
    }

    @Override
    public Operand rename(Function<Temp, Temp> sigma) {
      return this;
    }
  }

  final static class Reg extends Operand {

    public Temp reg;

    public Reg(Temp reg) {
      assert (reg != null);
      this.reg = reg;
    }

    @Override
    public Operand rename(Function<Temp, Temp> sigma) {
      return new Reg(sigma.apply(reg));
    }
  }

  final static class Mem extends Operand {

    public final Temp base;  // maybe null
    public final Integer scale; // null or 1, 2, 4 or 8;
    public final Temp index;  // maybe null
    public final int displacement;

    public Mem(Temp base, Integer scale, Temp index, int displacement) {
      assert (scale == null || (scale == 1 || scale == 2 || scale == 4 || scale == 8));
      this.base = base;
      this.scale = scale;
      this.index = index;
      this.displacement = displacement;
    }

    public Mem(Temp base) {
      this(base, null, null, 0);
    }

    @Override
    public Operand rename(Function<Temp, Temp> sigma) {
      return new Mem(base != null ? sigma.apply(base) : null, scale,
              index != null ? sigma.apply(index) : null, displacement);
    }
  }

  public abstract Operand rename(Function<Temp, Temp> sigma);
}
