package minijava.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SimpleGraph<NodeInfo> {

	private final Map<NodeInfo, Node<NodeInfo>> nodes = new HashMap<>();

	private final String name;
	private final boolean directed;

	public static class BackupNode<T> {
		private final T info;
		private final Set<T> successors = new HashSet<>();
		private final Set<T> predecessors = new HashSet<>();

		private BackupNode(Node<T> node) {
			info = node.info;
			for (Node<T> s : node.successors()) {
				successors.add(s.info);
			}
			for (Node<T> p : node.predecessors()) {
				predecessors.add(p.info);
			}
		}
	}

	public static class Node<T> {

		public T info;
		private Set<Node<T>> successors;
		private Set<Node<T>> predecessors;

		private Node(T info) {
			this.info = info;
			this.successors = new HashSet<>();
			this.predecessors = new HashSet<>();
		}

		/**
		 * n.successors() gibt die Menge aller Nachfolger des Knotes n zurueck,
		 * d.h. die Menge {n | (n, m) in E}
		 */
		public Set<Node<T>> successors() {
			return Collections.unmodifiableSet(successors);
		}

		/**
		 * n.predecessors() gibt die Menge aller Nachfolger des Knotes n
		 * zurueck, d.h. die Menge {m | (m, n) in E}
		 */
		public Set<Node<T>> predecessors() {
			return Collections.unmodifiableSet(predecessors);
		}

		public Set<Node<T>> neighbours() {
			Set<Node<T>> neigbours = new HashSet<>();
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
			return (obj instanceof SimpleGraph.Node && ((Node<T>)obj).info.equals(info));
		}

		@Override
		public int hashCode() {
			return info.hashCode();
		}

		public BackupNode<T> backup() {
			return new BackupNode<T>(this);
		}

		// TODO: Should not be necessary
		protected void addSuccessor(Node<T> successor) {
			successors.add(successor);
		}

		// TODO: Should not be necessary
		protected void addPredecessor(Node<T> predecessor) {
			predecessors.add(predecessor);
		}

		// TODO: Should not be necessary
		protected void removeSuccessor(Node<T> successor) {
			successors.remove(successor);
		}

		// TODO: Should not be necessary
		protected void removePredecessor(Node<T> predecessor) {
			predecessors.remove(predecessor);
		}

		protected void reverse() {
			Set<Node<T>> temp = successors;
			successors = predecessors;
			predecessors = temp;
		}
	}

	public SimpleGraph(String name, boolean directed) {
		this.name = name;
		this.directed = directed;
	}

	public String getName() {
		return name;
	}

	public Set<Node<NodeInfo>> nodeSet() {
		return new HashSet<>(nodes.values());
	}

	public void removeNode(Node<NodeInfo> n) {
		nodes.remove(n.info);
		for (Node<NodeInfo> successor : n.successors()) {
			successor.removePredecessor(n);
		}

		for (Node<NodeInfo> predecessor : n.predecessors()) {
			predecessor.removeSuccessor(n);
		}
	}

	public Node<NodeInfo> addNode(NodeInfo info) {
		Node<NodeInfo> node = new Node<NodeInfo>(info);
		nodes.put(info, node);
		return node;
	}

	public Node<NodeInfo> get(NodeInfo info) {
		return nodes.get(info);
	}

	public void restore(BackupNode<NodeInfo> node) {
		Node<NodeInfo> n = addNode(node.info);
		for (NodeInfo st : node.successors) {
			Node<NodeInfo> s = nodes.get(st);
			if (s != null) {
				addEdge(n, s);
			}
		}
		for (NodeInfo pt : node.predecessors) {
			Node<NodeInfo> p = nodes.get(pt);
			if (p != null) {
				addEdge(p, n);
			}
		}
	}

	public Map<NodeInfo, BackupNode<NodeInfo>> backup() {
		Map<NodeInfo, BackupNode<NodeInfo>> backup = new HashMap<>();
		for (NodeInfo info : nodes.keySet()) {
			backup.put(info, nodes.get(info).backup());
		}
		return backup;
	}

	public void addEdge(Node<NodeInfo> src, Node<NodeInfo> dst) {
		src.addSuccessor(dst);
		dst.addPredecessor(src);
	}

	public void removeEdge(Node<NodeInfo> src, Node<NodeInfo> dst) {
		src.removeSuccessor(dst);
		dst.removePredecessor(src);
	}

	public void reverse() {
		for (Node<NodeInfo> node : nodes.values()) {
			node.reverse();
		}
	}

	/**
	 * Prints the graph in dot representation. The output can be opened with
	 * dotty (<code>dotty output.dot</code>) or converted to PDF with dot (
	 * <code>dot -Tpdf output.dot > output.pdf</code>). See:
	 * http://www.graphviz.com
	 */
	public String getDot() {
		
		String graphType = (directed) ? "digraph" : "graph";
		String lineType = (directed) ? "->" : "--";
		
		StringBuilder out = new StringBuilder();
		
		out
			.append(graphType)
			.append(" G {" + System.lineSeparator());

		for (Node<NodeInfo> n : nodes.values()) {
			out.append("\"" + n.hashCode() + "\" [label=\"" + n.info.toString()
					+ "\"];" + System.lineSeparator());
		}
		for (Node<NodeInfo> n : nodes.values()) {
			for (Node<NodeInfo> m : n.successors()) {
				out.append("\"" + n.hashCode() + "\" " + lineType + " \"" + m.hashCode()
						+ "\";" + System.lineSeparator());
			}
		}
		
		out
			.append("labelloc=\"t\";" + System.lineSeparator())
			.append("label=\"")
			.append(name)
			.append("\";" + System.lineSeparator())
			.append("}" + System.lineSeparator());
		return out.toString();
	}
}
