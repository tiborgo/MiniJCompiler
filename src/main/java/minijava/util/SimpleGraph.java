package minijava.util;

import java.io.PrintStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SimpleGraph<NodeInfo> {

  private final Set<Node> nodes = new HashSet<>();
  private Map<Node, Set<Node>> successors = new HashMap<>();
  private Map<Node, Set<Node>> predecessors = new HashMap<>();

  public class Node {

    public NodeInfo info;

    public Node(NodeInfo info) {
      this.info = info;
      nodes.add(this);
      successors.put(this, new HashSet<Node>());
      predecessors.put(this, new HashSet<Node>());
    }

    /**
     * n.successors() gibt die Menge aller Nachfolger des Knotes n zurueck,
     * d.h. die Menge {n | (n, m) in E}
     */
    public Set<Node> successors() {
      return Collections.unmodifiableSet(successors.get(this));
    }

    /**
     * n.predecessors() gibt die Menge aller Nachfolger des Knotes n zurueck,
     * d.h. die Menge {m | (m, n) in E}
     */
    public Set<Node> predecessors() {
      return Collections.unmodifiableSet(predecessors.get(this));
    }

    public Set<Node> neighbours() {
      Set<Node> neigbours = new HashSet<>();
      neigbours.addAll(successors.get(this));
      neigbours.addAll(predecessors.get(this));
      return neigbours;
    }

    public int inDegree() {
      return predecessors.get(this).size();
    }

    public int outDegree() {
      return successors.get(this).size();
    }

    public int degree() {
      return inDegree() + outDegree();
    }
  }

  public Set<Node> nodeSet() {
    return Collections.unmodifiableSet(nodes);
  }

  public void removeNode(Node n) {
    nodes.remove(n);
    successors.remove(n);
    predecessors.remove(n);
    for (Node m : nodes) {
      successors.get(m).remove(n);
      predecessors.get(m).remove(n);
    }
  }

  public void addEdge(Node src, Node dst) {
    successors.get(src).add(dst);
    predecessors.get(dst).add(src);
  }

  public void removeEdge(Node src, Node dst) {
    successors.get(src).remove(dst);
    predecessors.get(dst).remove(src);
  }

  public void reverse() {
    Map<Node, Set<Node>> m = successors;
    successors = predecessors;
    predecessors = m;
  }

  /**
   * Prints the graph in dot representation. The output can be opened
   * with dotty (<code>dotty output.dot</code>) or converted to PDF
   * with dot (<code>dot -Tpdf output.dot > output.pdf</code>).
   * See: http://www.graphviz.com
   */
  public void printDot(PrintStream out) {
    out.println("digraph G {");
    for (Node n : nodes) {
      out.println("\"" + n + "\" [label=\"" + n.info.toString() + "\"];");
    }
    for (Node n : nodes) {
      for (Node m : n.successors()) {
        out.println("\"" + n + "\"  -> \"" + m + "\"");
      }
    }
    out.println("}");
  }
}
