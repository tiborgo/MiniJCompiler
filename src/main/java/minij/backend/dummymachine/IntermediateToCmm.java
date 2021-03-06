/*
 * MiniJCompiler: Compiler for a subset of the Java(R) language
 *
 * (C) Copyright 2014-2015 Tibor Goldschwendt <goldschwendt@cip.ifi.lmu.de>,
 * Michael Seifert <mseifert@error-reports.org>
 *
 * This file is part of MiniJCompiler.
 *
 * MiniJCompiler is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MiniJCompiler is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MiniJCompiler.  If not, see <http://www.gnu.org/licenses/>.
 */
package minij.backend.dummymachine;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import minij.translate.layout.Fragment;
import minij.translate.layout.FragmentProc;
import minij.translate.layout.FragmentVisitor;
import minij.translate.layout.Temp;
import minij.translate.tree.TreeExp;
import minij.translate.tree.TreeExpCALL;
import minij.translate.tree.TreeExpCONST;
import minij.translate.tree.TreeExpESEQ;
import minij.translate.tree.TreeExpMEM;
import minij.translate.tree.TreeExpNAME;
import minij.translate.tree.TreeExpOP;
import minij.translate.tree.TreeExpTEMP;
import minij.translate.tree.TreeExpVisitor;
import minij.translate.tree.TreeStm;
import minij.translate.tree.TreeStmCJUMP;
import minij.translate.tree.TreeStmEXP;
import minij.translate.tree.TreeStmJUMP;
import minij.translate.tree.TreeStmLABEL;
import minij.translate.tree.TreeStmMOVE;
import minij.translate.tree.TreeStmSEQ;
import minij.translate.tree.TreeStmVisitor;

public class IntermediateToCmm {
	
	private final static String valueType = "int32_t";

  public static String stmFragmentsToCmm(List<? extends Fragment<TreeStm>> frags) {
    List<Fragment<List<TreeStm>>> frag1 = new LinkedList<>();
    for (Fragment<TreeStm> frag : frags) {
      frag1.add(frag.accept(new FragmentVisitor<TreeStm, Fragment<List<TreeStm>>>() {

        @Override
        public Fragment<List<TreeStm>> visit(FragmentProc<TreeStm> fragProc) {
          return new FragmentProc<>(fragProc.frame, 
                       Collections.singletonList(fragProc.body));
        }
      }));
    }

    return stmListFragmentsToCmm(frag1);
  }

  public static String stmListFragmentsToCmm(List<? extends Fragment<List<TreeStm>>> frags) {
    declarations = new StringBuilder();
    code = new StringBuilder();

    declarations.append("#include <stdint.h>\n");
    declarations.append("#include \"runtime_32.h\"\n");
    declarations.append("#define MEM(x) *((" + valueType + "*)(x))\n\n");

    for (Fragment<List<TreeStm>> f : frags) {
      f.accept(new FragmentToCmm());
    }

    return declarations.toString() + "\n\n" + code.toString();
  }

  private static class FragmentToCmm implements FragmentVisitor<List<TreeStm>, Void> {

    public FragmentToCmm() {
    }

    @Override
    public Void visit(FragmentProc<List<TreeStm>> fragProc) {

      if (!(fragProc.frame instanceof DummyMachineFrame)) {
        throw new UnsupportedOperationException("Can only print intermediate code generated for dummy machine.");
      }
      DummyMachineFrame frame = (DummyMachineFrame) fragProc.frame;

      // function prototype
      StringBuilder prototype = new StringBuilder();
      prototype.append(valueType + " ").append(frame.getName()).append("(");
      String sep = "";
      for (Temp t : frame.params) {
        prototype.append(sep).append(valueType + " ").append(t);
        sep = ", ";
      }
      prototype.append(")");

      declarations.append(prototype);
      declarations.append(";\n");

      code.append(prototype);
      code.append(" {\n");
      codeIndent++;

      // declare locals
      Set<Temp> temps = new TreeSet<Temp>();
      TempsInTreeStm tempsv = new TempsInTreeStm();
      for (TreeStm stm : fragProc.body) {
        temps.addAll(stm.accept(tempsv));
      }
      temps.removeAll(frame.params);
      if (temps.size() > 0) {
        String decls = "";
        sep = "";
        for (Temp t : temps) {
          decls += sep + t;
          sep = ", ";
        }
        emit(valueType + " " + decls + ";");
      }

      // function body
      TreeStmToCmm tv = new TreeStmToCmm();
      for (TreeStm stm : fragProc.body) {
        emit("/* " + stm.toString() + " */");
        stm.accept(tv);
      }

      // return
      emit("return " + DummyMachineFrame.returnReg + ";");
      codeIndent--;
      emit("}");
      code.append("\n");
      return null;
    }
  }

  private static StringBuilder declarations = null;
  private static StringBuilder code = null;
  private static int codeIndent = 0;

  private static void emit(String s) {
    for (int i = 0; i < codeIndent; i++) {
      code.append("  ");
    }
    code.append(s);
    code.append("\n");
  }
  
  private static Temp emitVarDecl(String v) {
    Temp t = new Temp();
    emit(valueType + " " + t + " = " + v + ";");
    return t;
  }

  private static class TreeExpToCmm implements TreeExpVisitor<String, RuntimeException> {

    private String gen(TreeExp e) {
      return e.accept(this);
    }

    private String genVarDecl(TreeExp e) {
        String v = e.accept(this); 
        if (e instanceof TreeExpCONST || e instanceof TreeExpNAME) return v;
        else return emitVarDecl(v).toString();
    }

    private Void gen(TreeStm s) {
      return s.accept(new TreeStmToCmm());
    }

    @Override
    public String visit(TreeExpCONST expCONST) {
      return "" + expCONST.value;
    }

    @Override
    public String visit(TreeExpNAME expNAME) {
      return "(" + valueType + ")" + expNAME.label.toString();
    }

    @Override
    public String visit(TreeExpTEMP expTEMP) {
      return expTEMP.temp.toString();
    }

    @Override
    public String visit(TreeExpMEM expMEM) {
      return "MEM(" + gen(expMEM.addr) + ")";
    }

    private String printOperator(TreeExpOP.Op op) {
      switch (op) {
        case PLUS:
          return "+";
        case MINUS:
          return "-";
        case MUL:
          return "*";
        case DIV:
          return "/";
        case AND:
          return "&";
        case OR:
          return "|";
        case LSHIFT:
          return "<<";
        case RSHIFT:
          return ">>";
        case XOR:
          return "^";
        default:
          throw new UnsupportedOperationException("Operator " + op + " not supported in C--");
      }
    }

    @Override
    public String visit(TreeExpOP expOP) {
      // C doesn't specify the evaluation ordering within expressions,
      // so we have to sequence possibly effectful expressions explicitly.
      String left = genVarDecl(expOP.left);
      String right = genVarDecl(expOP.right);
      return "(" + left + " " + printOperator(expOP.op) + " " + right + ")";
    }

    @Override
    public String visit(TreeExpCALL expCALL) {
      String call = "";
      String argTypes = "";
      String sep = "";
      for (TreeExp arg : expCALL.args) {
        call += sep + genVarDecl(arg);
        argTypes += sep + valueType;
        sep = ", ";
    }
      if (expCALL.func instanceof TreeExpNAME) {
        call = ((TreeExpNAME) expCALL.func).label + "(" + call + ")";
      } else {
        call = "((" + valueType + " (*) (" + argTypes + "))(" + gen(expCALL.func) + "))(" + call + ")";
      } 
      Temp r = emitVarDecl(call);
      return r.toString();
    }

    @Override
    public String visit(TreeExpESEQ expESEQ) {
      gen(expESEQ.stm);
      return gen(expESEQ.res);
    }
  }

  private static class TreeStmToCmm implements TreeStmVisitor<Void, RuntimeException> {

    private String gen(TreeExp e) {
      return e.accept(new TreeExpToCmm());
    }

    private String genVarDecl(TreeExp e) {
        String v = e.accept(new TreeExpToCmm()); 
        if (e instanceof TreeExpCONST || e instanceof TreeExpNAME) return v;
        else return emitVarDecl(v).toString();
    }

    private Void gen(TreeStm s) {
      return s.accept(this);
    }

    @Override
    public Void visit(TreeStmMOVE stmMOVE) {
      if (stmMOVE.dest instanceof TreeExpTEMP) {
        TreeExpTEMP dst = (TreeExpTEMP) stmMOVE.dest;
        String src = gen(stmMOVE.src);
        emit(dst.temp + " = " + src + ";");        
      } else if (stmMOVE.dest instanceof TreeExpMEM) {
        TreeExpMEM dst = (TreeExpMEM) stmMOVE.dest;
        String a = genVarDecl(dst.addr);
        String src = gen(stmMOVE.src);
        emit("MEM(" + a + ")" + " = " + src + ";");
      } else if (stmMOVE.dest instanceof TreeExpESEQ) {
        TreeExpESEQ dst = (TreeExpESEQ) stmMOVE.dest;
        new TreeStmSEQ(dst.stm, new TreeStmMOVE(dst.res, stmMOVE.src)).accept(this);
      } else {
        throw new UnsupportedOperationException("Left-hand side of MOVE must be TEMP, MEM or ESEQ!");
      }
      return null;
    }

    @Override
    public Void visit(TreeStmEXP stmEXP) {
      emit(gen(stmEXP.exp) + ";");
      return null;
    }

    @Override
    public Void visit(TreeStmJUMP stmJUMP) {
      if (stmJUMP.dest instanceof TreeExpNAME) {
        emit("goto " + ((TreeExpNAME) stmJUMP.dest).label + ";");
      } else {
        throw new UnsupportedOperationException("Only calls to labels are implemented!");
      }
      return null;
    }

    private String printRelation(TreeStmCJUMP.Rel rel) {
      switch (rel) {
        case EQ:
          return "==";
        case NE:
          return "!=";
        case LT:
          return "<";
        case GT:
          return ">";
        case LE:
          return "<=";
        case GE:
          return ">=";
        default:
          throw new UnsupportedOperationException("CJUMP relation " + rel + " not supported in C--");
      }
    }

    @Override
    public Void visit(TreeStmCJUMP stmCJUMP) {
      // C doesn't specify the evaluation ordering within expressions,
      // so we have to sequence possibly effectful expressions explicitly.
      String left  = genVarDecl(stmCJUMP.left);
      String right = genVarDecl(stmCJUMP.right);

      emit("if ("
              + left + " "
              + printRelation(stmCJUMP.rel) + " "
              + right + ") "
              + "goto " + stmCJUMP.ltrue
              + "; else goto " + stmCJUMP.lfalse + ";");
      return null;
    }

    @Override
    public Void visit(TreeStmSEQ stmSEQ) {
      gen(stmSEQ.first);
      gen(stmSEQ.second);
      return null;
    }

    @Override
    public Void visit(TreeStmLABEL stmLABEL) {
      codeIndent--;
      emit(stmLABEL.label.toString() + ": ;");
      codeIndent++;
      return null;
    }
  }

  static class TempsInTreeExp implements TreeExpVisitor<Set<Temp>, RuntimeException> {

    private Set<Temp> temps(TreeExp e) {
      return e.accept(this);
    }

    private Set<Temp> temps(TreeStm s) {
      return s.accept(new TempsInTreeStm());
    }

    @Override
    public Set<Temp> visit(TreeExpCONST expCONST) {
      return new TreeSet<Temp>();
    }

    @Override
    public Set<Temp> visit(TreeExpNAME expNAME) {
      return new TreeSet<Temp>();
    }

    @Override
    public Set<Temp> visit(TreeExpTEMP expTEMP) {
      Set<Temp> s = new TreeSet<Temp>();
      s.add(expTEMP.temp);
      return s;
    }

    @Override
    public Set<Temp> visit(TreeExpMEM expMEM) {
      return temps(expMEM.addr);
    }

    @Override
    public Set<Temp> visit(TreeExpOP expOP) {
      Set<Temp> s = temps(expOP.left);
      s.addAll(temps(expOP.right));
      return s;
    }

    @Override
    public Set<Temp> visit(TreeExpCALL expCALL) {
      Set<Temp> s = new TreeSet<Temp>();
      for (TreeExp arg : expCALL.args) {
        s.addAll(temps(arg));
      }
      return s;
    }

    @Override
    public Set<Temp> visit(TreeExpESEQ expESEQ) {
      Set<Temp> s = temps(expESEQ.stm);
      s.addAll(temps(expESEQ.res));
      return s;
    }
  }

  static class TempsInTreeStm implements TreeStmVisitor<Set<Temp>, RuntimeException> {

    private Set<Temp> temps(TreeExp e) {
      return e.accept(new TempsInTreeExp());
    }

    private Set<Temp> temps(TreeStm s) {
      return s.accept(this);
    }

    @Override
    public Set<Temp> visit(TreeStmMOVE stmMOVE) {
      Set<Temp> s = temps(stmMOVE.dest);
      s.addAll(temps(stmMOVE.src));
      return s;
    }

    @Override
    public Set<Temp> visit(TreeStmEXP stmEXP) {
      return temps(stmEXP.exp);
    }

    @Override
    public Set<Temp> visit(TreeStmJUMP stmJUMP) {
      return temps(stmJUMP.dest);
    }

    @Override
    public Set<Temp> visit(TreeStmCJUMP stmCJUMP) {
      Set<Temp> s = temps(stmCJUMP.left);
      s.addAll(temps(stmCJUMP.right));
      return s;
    }

    @Override
    public Set<Temp> visit(TreeStmSEQ stmSEQ) {
      Set<Temp> s = temps(stmSEQ.first);
      s.addAll(temps(stmSEQ.second));
      return s;
    }

    @Override
    public Set<Temp> visit(TreeStmLABEL stmLABEL) {
      return new TreeSet<Temp>();
    }
  }
}
