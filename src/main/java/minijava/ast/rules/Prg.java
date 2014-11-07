package minijava.ast.rules;

import java.util.List;

import minijava.ast.visitors.PrgVisitor;

public class Prg {

  final public DeclMain mainClass;
  final public List<DeclClass> classes;

  public Prg(DeclMain mainClass, List<DeclClass> classes) {
    this.mainClass = mainClass;
    this.classes = classes;
  }

  public <A, T extends Throwable> A accept(PrgVisitor<A, T> v) throws T {
    return v.visit(this);
  }
}
