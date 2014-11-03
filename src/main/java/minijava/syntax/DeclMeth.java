package minijava.syntax;

import java.util.List;

public class DeclMeth {

  final public Ty ty;
  final public String methodName;
  final public List<Parameter> parameters;
  final public List<DeclVar> localVars;
  final public Stm body;
  final public Exp returnExp;

  public DeclMeth(Ty ty,
          String methodName,
          List<Parameter> parameters,
          List<DeclVar> localVars,
          Stm body,
          Exp returnExp) {
    this.ty = ty;
    this.methodName = methodName;
    this.parameters = parameters;
    this.localVars = localVars;
    this.body = body;
    this.returnExp = returnExp;
  }
}

