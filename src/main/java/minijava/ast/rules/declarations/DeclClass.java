package minijava.ast.rules.declarations;

import java.util.List;

public class DeclClass extends Decl {

  final public String className;
  final public String superName; // null if no superclass
  final public List<DeclVar> fields;
  final public List<DeclMeth> methods;

  public DeclClass(String className, String superName,
          List<DeclVar> fields, List<DeclMeth> methods) {
    this.className = className;
    this.superName = superName;
    this.fields = fields;
    this.methods = methods;
  }

	@Override
	public <A, T extends Throwable> A accept(DeclVisitor<A, T> v) throws T {
		return v.visit(this);
	}
}
