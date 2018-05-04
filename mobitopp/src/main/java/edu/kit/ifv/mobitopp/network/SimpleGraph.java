package edu.kit.ifv.mobitopp.network;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import edu.kit.ifv.mobitopp.visum.VisumNode;
import edu.kit.ifv.mobitopp.visum.VisumOrientedLink;
import edu.kit.ifv.mobitopp.visum.VisumPoint2;
import edu.kit.ifv.mobitopp.visum.VisumTransportSystems;

public class SimpleGraph
	implements Graph, Serializable
{

	private static final long serialVersionUID = 1L;

	protected Map<Integer, Node> nodes;
	protected Map<Integer, Edge> edges;

	protected Map<Integer, Set<Edge>> in = new LinkedHashMap<Integer, Set<Edge>>();
	protected Map<Integer, Set<Edge>> out = new LinkedHashMap<Integer, Set<Edge>>();
	
	private final VisumTransportSystems transportSystems;

	public SimpleGraph(VisumTransportSystems transportSystems) {
		super();
		this.transportSystems = transportSystems;
		this.nodes = new LinkedHashMap<Integer, Node>();
		this.edges = new LinkedHashMap<Integer, Edge>();
	}

	public SimpleNode makeNode(int id, Point2D coord) {
		return new SimpleNode(id, coord);
	}

	protected SimpleNode makeNode(VisumNode vNode) {
		return new SimpleNode(vNode);
	}

	protected SimpleNode makeNode(int id, VisumPoint2 vPoint) {
		return new SimpleNode(id, vPoint);
	}

	public SimpleEdge makeEdge(
		Integer id, 
		Node from,
		Node to,
		VisumOrientedLink vLink
	) {
		SimpleEdge edge = new SimpleEdge(id, from, to, vLink, carAllowed(vLink));

		registerEdge(edge);

		return edge;
	}

	public SimpleEdge makeEdge(
		int id,
		SimpleNode node1,
		SimpleNode node2
	) {

		SimpleEdge e1 = new SimpleEdge( id, node1, node2, null, false);

		registerEdge(e1);

		return e1;
	}

	public SimpleEdge makeTwinEdge(
		int id,
		SimpleNode node1,
		SimpleNode node2
	) {

		SimpleEdge e1 = new SimpleEdge( id, node1, node2, null, false);
		SimpleEdge e2 = new SimpleEdge(-id, node2, node1, null, false);
		e1.twin = e2;
		e2.twin = e1;

		registerEdge(e1);
		registerEdge(e2);

		return e1;
	}

	protected boolean carAllowed(VisumOrientedLink vLink) {
		if (vLink == null) {
			return false;
		}
		return vLink.transportSystems.transportSystems.contains(transportSystems.getBy("P"));
	}

	protected void registerEdge(SimpleEdge edge) {

		Node from = edge.from();
		Node to = edge.to();

		if (!this.out.containsKey(from.id())) {	
			this.out.put(from.id(), new LinkedHashSet<Edge>());
		}

		if (!this.in.containsKey(to.id())) {	
			this.in.put(to.id(), new LinkedHashSet<Edge>());
		}

		boolean edgeIsNewAtFrom = this.out.get(from.id()).add(edge);
		boolean edgeIsNewAtTo 	= this.in.get(to.id()).add(edge);

		assert edgeIsNewAtFrom;
		assert edgeIsNewAtTo;
	}


	public Collection<Node> nodes() {
		return this.nodes.values();
	}

	public Collection<Edge> edges() {
		return this.edges.values();
	}

	public Node node(int id) {
		return this.nodes.get(id);
	}

	public Edge edge(int id) {
		return this.edges.get(id);
	}

	public Node from(Edge e) {
		return e.from();
	}

	public Node to(Edge e) {
		return e.to();
	}

	public Edge twin(Edge e) {
		return e.twin();
	}

	public Collection<Edge> in(Node n) {
		return new ArrayList<Edge>(this.in.get(n.id()));
	}

	public Collection<Edge> out(Node n)  {
		return new ArrayList<Edge>(this.out.get(n.id()));
	}

	///// END Network Interface //////////

}
