package minijava.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SimpleGraph<NodeInfo> {

	private final Map<NodeInfo, Node> nodes = new HashMap<>();

	private Map<Node, Set<Node>> successors = new HashMap<>();
	private Map<Node, Set<Node>> predecessors = new HashMap<>();

	private final String name;

	public class BackupNode {
		private final NodeInfo info;
		private final Set<NodeInfo> successors = new HashSet<>();
		private final Set<NodeInfo> predecessors = new HashSet<>();

		private BackupNode(Node node) {
			info = node.info;
			for (Node s : node.successors()) {
				successors.add(s.info);
			}
			for (Node p : node.predecessors()) {
				predecessors.add(p.info);
			}
		}
	}

	public class Node {

		public NodeInfo info;
		private final Set<Node> successors;
		private final Set<Node> predecessors;

		private Node(NodeInfo info, Set<Node> successors, Set<Node> predecessors) {
			this.info = info;
			// TODO: Make defensive copy
			this.successors = successors;
			this.predecessors = predecessors;
		}

		/**
		 * n.successors() gibt die Menge aller Nachfolger des Knotes n zurueck,
		 * d.h. die Menge {n | (n, m) in E}
		 */
		public Set<Node> successors() {
			return Collections.unmodifiableSet(successors);
		}

		/**
		 * n.predecessors() gibt die Menge aller Nachfolger des Knotes n
		 * zurueck, d.h. die Menge {m | (m, n) in E}
		 */
		public Set<Node> predecessors() {
			return Collections.unmodifiableSet(predecessors);
		}

		public Set<Node> neighbours() {
			Set<Node> neigbours = new HashSet<>();
			neigbours.addAll(successors);
			neigbours.addAll(predecessors);
			return neigbours;
		}

		public int inDegree() {
			return predecessors.size();
		}

		public int outDegree() {
			return successors.size();
		}

		public int degree() {
			return inDegree() + outDegree();
		}

		@Override
		public String toString() {
			return "Node: " + info.toString();
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean equals(Object obj) {
			return (obj instanceof SimpleGraph.Node && ((Node)obj).info.equals(info));
		}

		@Override
		public int hashCode() {
			return info.hashCode();
		}

		public BackupNode backup() {
			return new BackupNode(this);
		}
	}

	public SimpleGraph(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public Set<Node> nodeSet() {
		return new HashSet<>(nodes.values());
	}

	public void removeNode(Node n) {
		nodes.remove(n.info);
		successors.remove(n);
		predecessors.remove(n);
		for (Node m : nodes.values()) {
			successors.get(m).remove(n);
			predecessors.get(m).remove(n);
		}
	}

	public Node addNode(NodeInfo info) {
		Set<Node> successorSet = new HashSet<Node>();
		Set<Node> predecessorSet = new HashSet<Node>();
		Node node = new Node(info, successorSet, predecessorSet);
		nodes.put(info, node);
		successors.put(node, successorSet);
		predecessors.put(node, predecessorSet);
		return node;
	}

	public Node get(NodeInfo info) {
		return nodes.get(info);
	}

	public void restore(BackupNode node) {
		Node n = addNode(node.info);
		for (NodeInfo st : node.successors) {
			Node s = nodes.get(st);
			if (s != null) {
				addEdge(n, s);
			}
		}
		for (NodeInfo pt : node.predecessors) {
			Node p = nodes.get(pt);
			if (p != null) {
				addEdge(p, n);
			}
		}
	}

	public Map<NodeInfo, BackupNode> backup() {
		Map<NodeInfo, BackupNode> backup = new HashMap<>();
		for (NodeInfo info : nodes.keySet()) {
			backup.put(info, nodes.get(info).backup());
		}
		return backup;
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
	 * Prints the graph in dot representation. The output can be opened with
	 * dotty (<code>dotty output.dot</code>) or converted to PDF with dot (
	 * <code>dot -Tpdf output.dot > output.pdf</code>). See:
	 * http://www.graphviz.com
	 */
	public String getDot() {
		StringBuilder out = new StringBuilder();
		out.append("digraph G {" + System.lineSeparator());
		for (Node n : nodes.values()) {
			out.append("\"" + n.hashCode() + "\" [label=\"" + n.info.toString()
					+ "\"];" + System.lineSeparator());
		}
		for (Node n : nodes.values()) {
			for (Node m : n.successors()) {
				out.append("\"" + n.hashCode() + "\"  -> \"" + m.hashCode()
						+ "\"" + System.lineSeparator());
			}
		}
		out.append("}" + System.lineSeparator());
		return out.toString();
	}
}
