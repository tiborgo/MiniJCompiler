package minijava.ast.rules.declarations;

import java.util.List;

public class Class extends Declaration {

  final public String className;
  final public String superName; // null if no superclass
  final public List<Variable> fields;
  final public List<Method> methods;

  public Class(String className, String superName,
               List<Variable> fields, List<Method> methods) {
    this.className = className;
    this.superName = superName;
    this.fields = fields;
    this.methods = methods;
  }

	@Override
	public <A, T extends Throwable> A accept(DeclarationVisitor<A, T> v) throws T {
		return v.visit(this);
	}
}
