package minijava.ast.rules;

import java.util.List;

import minijava.ast.visitors.PrettyPrintVisitor;

public class Prg {

  final public DeclMain mainClass;
  final public List<DeclClass> classes;

  public Prg(DeclMain mainClass, List<DeclClass> classes) {
    this.mainClass = mainClass;
    this.classes = classes;
  }

  public String prettyPrint() {
    return PrettyPrintVisitor.prettyPrint(this);
  }
}
