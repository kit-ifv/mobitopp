package edu.kit.ifv.mobitopp.network;

import java.util.Collection;

public interface Graph {


	public Collection<Node> nodes();
	public Collection<Edge> edges();

	public Node node(int id);
	public Edge edge(int id);

	public Node from(Edge e);
	public Node to(Edge e);

	public Edge twin(Edge e);

	public Collection<Edge> in(Node n);
	public Collection<Edge> out(Node n);
}

