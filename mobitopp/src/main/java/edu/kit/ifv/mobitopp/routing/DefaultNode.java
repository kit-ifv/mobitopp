package edu.kit.ifv.mobitopp.routing;

import java.util.Collection;

class DefaultNode
	implements Node, Comparable<Node>
{

	private final String id;

	private Edge[] inEdges = new Edge[0];
	private Edge[] outEdges = new Edge[0];


	public DefaultNode(String id) {
		this.id = id;
	}

	public String id() {
		return id;
	}

	public Edge[] incomingEdges() {

		return this.inEdges;
	}

	public Edge[] outgoingEdges() {

		return this.outEdges;
	}

	public boolean isSink() {
		return false;
	}


	public void setIncomingLinks(Collection<Link> incoming) {
		assert incoming != null;

		this.inEdges = incoming.toArray(new Edge[0]);
	}

	public void setOutgoingLinks(Collection<Link> outgoing) {
		assert outgoing != null;

		this.outEdges = outgoing.toArray(new Edge[0]);
	}

	public String toString() {
		return "(" + id + ")";
	}

	public int compareTo(Node o) {

		return id.compareTo(o.id());
	}

	public boolean isTarget() {
		return false;
	}

}
