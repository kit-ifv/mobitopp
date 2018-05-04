package edu.kit.ifv.mobitopp.routing;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;

public class DefaultGraph
	implements Graph 
{

	protected Node[] nodes;
	protected Edge[] edges;

	public DefaultGraph(
		Collection<DefaultNode> nodes,
		Collection<DefaultLink> edges
	) {

		this.nodes = nodes.toArray(new Node[0]);
		this.edges = edges.toArray(new Edge[0]);

	}


	public Node[] nodes() {
		return nodes;
	}

	public Edge[] edges() {
		return edges; 
	}


	public Edge[] outgoingEdges(Node node) {
		assert node instanceof DefaultNode;

		return new ArrayList<Edge>(Arrays.asList(node.outgoingEdges())).toArray(new Edge[0]);
	}

	public Edge[] incomingEdges(Node node) {
		assert node instanceof DefaultNode;

		return new ArrayList<Edge>(Arrays.asList(node.incomingEdges())).toArray(new Edge[0]);
	}

	public Node from(Edge edge) 
	{
		assert edge instanceof DefaultLink;

		return ((DefaultLink)edge).from();
	}

	public Node to(Edge edge)
	{
		assert edge instanceof DefaultLink;

		return ((DefaultLink)edge).to();
	}

	public float cost(Edge edge) {
		assert edge instanceof DefaultLink;

		return ((DefaultLink)edge).distance();
	}

}
