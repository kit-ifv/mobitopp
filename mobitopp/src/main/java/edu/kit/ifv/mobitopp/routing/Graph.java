package edu.kit.ifv.mobitopp.routing;

public interface Graph {

	public Node[] nodes();

	public Edge[] outgoingEdges(Node node);

	public Edge[] incomingEdges(Node node);

	public Node from(Edge edge);

	public Node to(Edge edge);

	public float cost(Edge edge);

}
