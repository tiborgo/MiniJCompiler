package minij.parse.rules.statements;

import java.util.List;

public class StatementList extends Statement {

  final public List<Statement> statements;

  public StatementList(List<Statement> statements) {
    this.statements = statements;
  }

  @Override
  public <A, T extends Throwable> A accept(StatementVisitor<A, T> v) throws T {
    return v.visit(this);
  }
}

