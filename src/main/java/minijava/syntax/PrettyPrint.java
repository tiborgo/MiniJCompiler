package minijava.syntax;

import java.util.List;

public class PrettyPrint {

  private static final String indentStep = "  ";

  public static String prettyPrint(Prg p) {
    return prettyPrintMain(p.mainClass, "") + "\n" +
            prettyPrintClassList(p.classes, "");
  }

  private static String prettyPrintClass(DeclClass c, String indent) {
    return indent + "class " + c.className  +
            (c.superName == null ? " " : " extends " + c.superName + " ") + "{\n\n" +
            prettyPrintVarList(c.fields, indent + indentStep) + 
            prettyPrintMethList(c.methods, indent + indentStep) +
            indent + "}\n";
  }

  private static String prettyPrintClassList(List<DeclClass> cl, String indent) {
    StringBuffer classes = new StringBuffer();
    String sep = "";
    for (DeclClass d : cl) {
      classes.append(sep);
      classes.append(prettyPrintClass(d, indent));
      sep = "\n";
    }
    return classes.toString();
  }

  private static String prettyPrintMeth(DeclMeth m, String indent) {
    String params = "", sep = "";
    for (Parameter p : m.parameters) {
      params += sep + p.ty.accept(new PrettyPrintVisitorTy()) + " " + p.id;
      sep = ", ";
    }

    return indent + "public " +
            m.ty.accept(new PrettyPrintVisitorTy()) + " " + m.methodName +
            " (" + params + ") {\n" +
            prettyPrintVarList(m.localVars, indent + indentStep) +
            m.body.accept(new PrettyPrintVisitorStm(indent + indentStep)) +
            indent + indentStep +
            "return " + m.returnExp.accept(new PrettyPrintVisitorExp()) + ";\n" +
            indent + "}\n";
  }

  private static String prettyPrintMethList(List<DeclMeth> dm, String indent) {
    StringBuffer meths = new StringBuffer();
    for (DeclMeth m : dm) {
      meths.append("\n");
      meths.append(prettyPrintMeth(m, indent));
    }
    return meths.toString();
  }

  private static String prettyPrintVar(DeclVar d, String indent) {
    return indent + d.ty.accept(new PrettyPrintVisitorTy()) + " " +
            d.name.toString() + ";";
  }

  private static String prettyPrintVarList(List<DeclVar> dl, String indent) {
    StringBuffer decls = new StringBuffer();
    for (DeclVar d : dl) {
      decls.append(prettyPrintVar(d, indent));
      decls.append("\n");
    }
    return decls.toString();
  }

  private static String prettyPrintMain(DeclMain d, String indent) {
    return indent + "class " + d.className.toString() + " {\n" +
            indent + indentStep + "public static void main (String[] " +
            d.mainArg.toString() + ") {\n" +
            d.mainBody.accept(new PrettyPrintVisitorStm(indent + indentStep + indentStep)) +
            indent + indentStep + "}\n" +
            indent + "}\n";
  }

  static class PrettyPrintVisitorTy implements TyVisitor<String> {

    @Override
    public String visit(TyVoid b) {
      return "void";
    }

    @Override
    public String visit(TyBool b) {
      return "boolean";
    }

    @Override
    public String visit(TyInt i) {
      return "int";
    }

    @Override
    public String visit(TyClass x) {
      return x.c.toString();
    }

    @Override
    public String visit(TyArr x) {
      return x.ty.accept(this) + "[]";
    }
  }

  static class PrettyPrintVisitorExp implements ExpVisitor<String, RuntimeException> {

    @Override
    public String visit(ExpTrue x) {
      return "true";
    }

    @Override
    public String visit(ExpFalse x) {
      return "false";
    }

    @Override
    public String visit(ExpThis x) {
      return "this";
    }

    @Override
    public String visit(ExpNewIntArray x) {
      return "new int [" + x.size.accept(this) + "]";
    }

    @Override
    public String visit(ExpNew x) {
      return "new " + x.className.toString() + "()";
    }

    @Override
    public String visit(ExpBinOp e) {
      return "(" + e.left.accept(this) + e.op.toString() + e.right.accept(this) + ")";
    }

    @Override
    public String visit(ExpArrayGet e) {
      return e.array.accept(this) + "[" + e.index.accept(this) + "]";
    }

    @Override
    public String visit(ExpArrayLength e) {
      return e.array.accept(this) + ".length";
    }

    @Override
    public String visit(ExpInvoke e) {
      String args = "";
      String sep = "";
      for (Exp ea : e.args) {
        args += sep + ea.accept(new PrettyPrintVisitorExp());
        sep = ", ";
      }

      return e.obj.accept(this) + "." + e.method + "(" + args + ")";
    }

    @Override
    public String visit(ExpIntConst x) {
      return (new Integer(x.value)).toString();
    }

    @Override
    public String visit(ExpId x) {
      return x.id;
    }

    @Override
    public String visit(ExpNeg x) {
      return "!(" + x.body.accept(this) + ")";
    }
  }

  static class PrettyPrintVisitorStm implements StmVisitor<String, RuntimeException> {

    final String indent;

    public PrettyPrintVisitorStm() {
      this.indent = "";
    }

    public PrettyPrintVisitorStm(String indent) {
      this.indent = indent;
    }

    @Override
    public String visit(StmList slist) {
      StringBuffer str = new StringBuffer();
      for (Stm s : slist.stms) {
        str.append(s.accept(new PrettyPrintVisitorStm(indent)));
      }
      return str.toString();
    }

    @Override
    public String visit(StmIf s) {
      return indent + "if (" + s.cond.accept(new PrettyPrintVisitorExp()) + ") {\n" +
              s.bodyTrue.accept(new PrettyPrintVisitorStm(this.indent + indentStep)) +
              indent + "} else {\n" +
              s.bodyFalse.accept(new PrettyPrintVisitorStm(this.indent + indentStep)) +
              indent + "}\n";
    }

    @Override
    public String visit(StmWhile s) {

      return indent + "while (" +
              s.cond.accept(new PrettyPrintVisitorExp()) + ") {\n" +
              s.body.accept(new PrettyPrintVisitorStm(this.indent + " "))  +
              indent + "}\n";
    }

    @Override
    public String visit(StmPrintlnInt s) {
      return indent + "System.out.println(" +
              s.arg.accept(new PrettyPrintVisitorExp()) + ");\n";
    }

    @Override
    public String visit(StmPrintChar s) {
      return indent + "System.out.print((char)" +
              s.arg.accept(new PrettyPrintVisitorExp()) + ");\n";
    }

    @Override
    public String visit(StmAssign s) {
      return indent + s.id + " = " +
              s.rhs.accept(new PrettyPrintVisitorExp()) + ";\n";
    }

    @Override
    public String visit(StmArrayAssign s) {
      return indent + s.id +
              "[" + s.index.accept(new PrettyPrintVisitorExp()) + "] = " +
              s.rhs.accept(new PrettyPrintVisitorExp()) + ";\n";
    }
  }
}
