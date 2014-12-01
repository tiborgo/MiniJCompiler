package minijava.backend.i386;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import minijava.intermediate.Frame;
import minijava.intermediate.Temp;
import minijava.intermediate.Label;
import minijava.intermediate.tree.TreeExp;
import minijava.intermediate.tree.TreeExpTEMP;
import minijava.intermediate.tree.TreeStm;
import minijava.intermediate.tree.TreeStmMOVE;
import minijava.intermediate.tree.TreeStmSEQ;

final class I386Frame implements Frame {

  final Label name;
  final List<Temp> params;
  final List<Temp> locals;
  final static Temp returnReg = new Temp();

  I386Frame(I386Frame frame) {
    this.name = frame.name;
    this.params = new ArrayList<Temp>(frame.params);
    this.locals = new ArrayList<Temp>(frame.locals);
  }

  I386Frame(Label name, int paramCount) {
    this.name = name;
    this.params = new ArrayList<Temp>();
    this.locals = new LinkedList<Temp>();
    for (int i = 0; i < paramCount; i++) {
      this.params.add(new Temp());
    }
  }

  @Override
  public Label getName() {
    return name;
  }

  @Override
  public int getParameterCount() {
    return params.size();
  }

  @Override
  public TreeExp getParameter(int number) {
    Temp t = params.get(number);
    return (t == null) ? null : new TreeExpTEMP(t);
  }

  @Override
  public TreeExp addLocal(Location l) {
    Temp t = new Temp();
    locals.add(t);
    return new TreeExpTEMP(t);
  }

  @Override
  public TreeStm makeProc(TreeStm body, TreeExp returnValue) {
		// TODO: Save callee-save registers
		// TODO: Restore callee-save registers
    return new TreeStmSEQ(body,
            new TreeStmMOVE(new TreeExpTEMP(returnReg), returnValue));
  }

  @Override
  public Frame clone() {
    return new I386Frame(this);
  }

  @Override
  public int size() {
    return 0;
  }
}
