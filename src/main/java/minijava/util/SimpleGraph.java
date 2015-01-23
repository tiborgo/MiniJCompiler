package minijava.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import minijava.flowanalysis.CoalesceableTemp;
import minijava.util.SimpleGraph.Node;

public class SimpleGraph<NodeInfo> {

	private final Map<NodeInfo, Node<NodeInfo>> nodes = new HashMap<>();
	private final Map<NodeInfo, Node<NodeInfo>> deactivatedNodes = new HashMap<>();

	private final String name;
	private final boolean directed;
	private final boolean secondaryDirected;

	/*public static class BackupNode<T> {
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
	}*/

	public static class Node<T> {

		public T info;
		
		private Set<Node<T>> successors;
		private Set<Node<T>> predecessors;
		
		private Set<Node<T>> deactivatedSuccessors;
		private Set<Node<T>> deactivatedPredecessors;
		
		private Set<Node<T>> secondarySuccessors;
		private Set<Node<T>> secondaryPredecessors;

		private Node(T info) {
			this.info = info;
			this.successors = new HashSet<>();
			this.predecessors = new HashSet<>();
			this.deactivatedSuccessors = new HashSet<>();
			this.deactivatedPredecessors = new HashSet<>();
			this.secondarySuccessors = new HashSet<>();
			this.secondaryPredecessors = new HashSet<>();
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
		
		public Set<Node<T>> secondarySuccessors() {
			return Collections.unmodifiableSet(secondarySuccessors);
		}
		
		public Set<Node<T>> secondaryPredecessors() {
			return Collections.unmodifiableSet(secondaryPredecessors);
		}

		public Set<Node<T>> neighbours() {
			Set<Node<T>> neigbours = new HashSet<>();
			neigbours.addAll(successors);
			neigbours.addAll(predecessors);
			return neigbours;
		}
		
		public Set<Node<T>> secondaryNeighbours() {
			Set<Node<T>> neigbours = new HashSet<>();
			neigbours.addAll(secondarySuccessors);
			neigbours.addAll(secondaryPredecessors);
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

		/*public BackupNode<T> backup() {
			return new BackupNode<T>(this);
		}*/

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

	public SimpleGraph(String name, boolean directed, boolean secondaryDirected) {
		this.name = name;
		this.directed = directed;
		this.secondaryDirected = secondaryDirected;
	}

	public String getName() {
		return name;
	}

	public Set<Node<NodeInfo>> nodeSet() {
		return new HashSet<>(nodes.values());
	}
	
	public void deactivateNode(Node<NodeInfo> n) {
		nodes.remove(n.info);
		deactivatedNodes.put(n.info, n);
		
		Set<Node<NodeInfo>> successors = new HashSet<>(n.successors());
		for (Node<NodeInfo> successor : successors) {
			deactivateEdge(n, successor);
		}

		Set<Node<NodeInfo>> predecessors = new HashSet<>(n.predecessors());
		for (Node<NodeInfo> predecessor : predecessors) {
			deactivateEdge(predecessor, n);
		}
	}
	
	public void activateNode(Node<NodeInfo> n) {
		deactivatedNodes.remove(n.info);
		nodes.put(n.info, n);
		
		Set<Node<NodeInfo>> successors = new HashSet<>(n.deactivatedSuccessors);
		for (Node<NodeInfo> successor : successors) {
			if (nodes.get(successor.info) != null) {
				activateEdge(n, successor);
			}
		}

		Set<Node<NodeInfo>> predecessors = new HashSet<>(n.deactivatedPredecessors);
		for (Node<NodeInfo> predecessor : predecessors) {
			if (nodes.get(predecessor.info) != null) {
				activateEdge(predecessor, n);
			}
		}
	}

	public Node<NodeInfo> addNode(NodeInfo info) {
		Node<NodeInfo> node = new Node<NodeInfo>(info);
		nodes.put(info, node);
		return node;
	}

	public Node<NodeInfo> get(NodeInfo info) {
		Node<NodeInfo> node = nodes.get(info);
		if (node == null) {
			node = deactivatedNodes.get(info);
		}
		return node;
	}

	/*public void restore(BackupNode<NodeInfo> node) {
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
	}*/

	public void addEdge(Node<NodeInfo> src, Node<NodeInfo> dst) {
		if (deactivatedNodes.get(dst) != null) {
			src.deactivatedSuccessors.add(dst);
		}
		else {
			src.addSuccessor(dst);
		}
		if (deactivatedNodes.get(src) != null) {
			dst.deactivatedPredecessors.add(src);
		}
		else {
			dst.addPredecessor(src);
		}
	}
	
	public void addSecondaryEdge(Node<NodeInfo> src, Node<NodeInfo> dst) {
		src.secondarySuccessors.add(dst);
		dst.secondaryPredecessors.add(src);
	}
	
	public boolean hasEdge(Node<NodeInfo> src, Node<NodeInfo> dst) {
		if (directed) {
			return src.successors.contains(dst);
		}
		else {
			return src.successors.contains(dst) || src.predecessors.contains(dst);
		}
	}
	
	public boolean hasSecondaryEdge(Node<NodeInfo> src, Node<NodeInfo> dst) {
		if (secondaryDirected) {
			return src.secondarySuccessors.contains(dst);
		}
		else {
			return src.secondarySuccessors.contains(dst) || src.secondaryPredecessors.contains(dst);
		}
	}

	public void removeEdge(Node<NodeInfo> src, Node<NodeInfo> dst) {
		src.removeSuccessor(dst);
		dst.removePredecessor(src);
	}
	
	public void removeSecondaryEdge(Node<NodeInfo> src, Node<NodeInfo> dst) {
		src.secondarySuccessors.remove(dst);
		dst.secondaryPredecessors.remove(src);
	}
	
	private void deactivateEdge(Node<NodeInfo> src, Node<NodeInfo> dst) {
		src.successors.remove(dst);
		src.deactivatedSuccessors.add(dst);
		dst.predecessors.remove(src);
		dst.deactivatedPredecessors.add(src);
	}
	
	private void activateEdge(Node<NodeInfo> src, Node<NodeInfo> dst) {
		src.deactivatedSuccessors.remove(dst);
		src.successors.add(dst);
		dst.deactivatedPredecessors.remove(src);
		dst.predecessors.add(src);
	}

	public void merge(Node<NodeInfo> a, Node<NodeInfo> b, NodeInfo info) {

		Node<NodeInfo> ab = new Node<>(info);
		
		Set<Node<NodeInfo>> allPrimaryPredecessors = new HashSet<>();
		allPrimaryPredecessors.addAll(a.predecessors);
		allPrimaryPredecessors.addAll(b.predecessors);
		allPrimaryPredecessors.addAll(a.deactivatedPredecessors);
		allPrimaryPredecessors.addAll(b.deactivatedPredecessors);
		allPrimaryPredecessors.remove(a);
		allPrimaryPredecessors.remove(b);
		
		Set<Node<NodeInfo>> allPrimarySuccessors = new HashSet<>();
		allPrimarySuccessors.addAll(a.successors);
		allPrimarySuccessors.addAll(b.successors);
		allPrimarySuccessors.addAll(a.deactivatedSuccessors);
		allPrimarySuccessors.addAll(b.deactivatedSuccessors);
		if (!directed) {
			allPrimarySuccessors.removeAll(allPrimaryPredecessors);
		}
		allPrimarySuccessors.remove(a);
		allPrimarySuccessors.remove(b);
		
		Set<Node<NodeInfo>> allSecondaryPredecessors = new HashSet<>();
		allSecondaryPredecessors.addAll(a.secondaryPredecessors);
		allSecondaryPredecessors.addAll(b.secondaryPredecessors);
		allSecondaryPredecessors.remove(a);
		allSecondaryPredecessors.remove(b);
		
		Set<Node<NodeInfo>> allSecondarySuccessors = new HashSet<>();
		allSecondarySuccessors.addAll(a.secondarySuccessors);
		allSecondarySuccessors.addAll(b.secondarySuccessors);
		if (!secondaryDirected) {
			allSecondarySuccessors.removeAll(allSecondaryPredecessors);
		}
		allSecondarySuccessors.remove(a);
		allSecondarySuccessors.remove(b);
		
		removeNode(a);
		removeNode(b);
		
		for (Node<NodeInfo> predecessor : allPrimaryPredecessors) {
			addEdge(predecessor, ab);
		}
		for (Node<NodeInfo> successor : allPrimarySuccessors) {
			addEdge(ab, successor);
		}
		for (Node<NodeInfo> predecessor : allSecondaryPredecessors) {
			addSecondaryEdge(predecessor, ab);
		}
		for (Node<NodeInfo> successor : allSecondarySuccessors) {
			addSecondaryEdge(ab, successor);
		}
		
		nodes.put(info, ab);
	}
	
	private void removeNode(Node<NodeInfo> n) {
		nodes.remove(n.info);
		for (Node<NodeInfo> successor : n.successors()) {
			successor.predecessors.remove(n);
		}
		for (Node<NodeInfo> predecessor : n.predecessors()) {
			predecessor.successors.remove(n);
		}
		for (Node<NodeInfo> successor : n.deactivatedSuccessors) {
			successor.deactivatedPredecessors.remove(n);
		}
		for (Node<NodeInfo> predecessor : n.deactivatedPredecessors) {
			predecessor.deactivatedSuccessors.remove(n);
		}
		for (Node<NodeInfo> successor : n.secondarySuccessors()) {
			successor.secondaryPredecessors.remove(n);
		}
		for (Node<NodeInfo> predecessor : n.secondaryPredecessors()) {
			predecessor.secondarySuccessors.remove(n);
		}
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
		
		String primaryLineType = (directed) ? "" : "dir=none";
		String secondaryLineType = (secondaryDirected) ? "" :  "dir=none";
		
		StringBuilder out = new StringBuilder();
		
		out
			.append("digraph G {" + System.lineSeparator());

		for (Node<NodeInfo> n : nodes.values()) {
			out.append("\"" + n.hashCode() + "\" [label=\"" + n.info.toString()
					+ "\"];" + System.lineSeparator());
		}
		for (Node<NodeInfo> n : deactivatedNodes.values()) {
			out.append("\"" + n.hashCode() + "\" [label=\"" + n.info.toString()
					+ "\", color=gray];" + System.lineSeparator());
		}
		
		for (Node<NodeInfo> n : nodes.values()) {
			for (Node<NodeInfo> m : n.successors) {
				out.append("\"" + n.hashCode() + "\" -> \"" + m.hashCode()
						+ "\" [" + primaryLineType + "];" + System.lineSeparator());
			}
		}
		
		for (Node<NodeInfo> n : deactivatedNodes.values()) {
			for (Node<NodeInfo> m : n.successors) {
				out.append("\"" + n.hashCode() + "\" -> \"" + m.hashCode()
						+ "\" [" + primaryLineType + ", color=gray];" + System.lineSeparator());
			}
		}
		
		for (Node<NodeInfo> n : nodes.values()) {
			for (Node<NodeInfo> m : n.deactivatedSuccessors) {
				out.append("\"" + n.hashCode() + "\" -> \"" + m.hashCode()
						+ "\" [" + primaryLineType + ", color=gray];" + System.lineSeparator());
			}
		}
		
		for (Node<NodeInfo> n : deactivatedNodes.values()) {
			for (Node<NodeInfo> m : n.deactivatedSuccessors) {
				out.append("\"" + n.hashCode() + "\" -> \"" + m.hashCode()
						+ "\" [" + primaryLineType + ", color=gray];" + System.lineSeparator());
			}
		}
		
		for (Node<NodeInfo> n : nodes.values()) {
			for (Node<NodeInfo> m : n.secondarySuccessors) {
				out.append("\"" + n.hashCode() + "\" -> \"" + m.hashCode()
						+ "\" [style=dotted, " + secondaryLineType + "];" + System.lineSeparator());
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
