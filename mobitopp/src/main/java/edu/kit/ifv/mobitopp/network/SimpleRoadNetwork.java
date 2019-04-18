package edu.kit.ifv.mobitopp.network;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.simulation.IdSequence;
import edu.kit.ifv.mobitopp.visum.VisumConnector;
import edu.kit.ifv.mobitopp.visum.VisumLink;
import edu.kit.ifv.mobitopp.visum.VisumNode;
import edu.kit.ifv.mobitopp.visum.VisumPoint2;
import edu.kit.ifv.mobitopp.visum.VisumRoadNetwork;
import edu.kit.ifv.mobitopp.visum.VisumTransportSystem;
import edu.kit.ifv.mobitopp.visum.VisumZone;

public class SimpleRoadNetwork 
	extends SimpleGraph
	implements Graph, Serializable
{

	private static final long serialVersionUID = 1L;

	protected static IdSequence connectorIds = new IdSequence();

	private final Map<Integer, Zone> zones;

	public final Map<Integer, SimpleNode> zoneCenterNodes;
	public final Map<Integer, List<Edge>> connectorEdges;

	public SimpleRoadNetwork(VisumRoadNetwork network) {
		this(network, defaultCarOf(network));
	}

	private static VisumTransportSystem defaultCarOf(VisumRoadNetwork network) {
		return network.transportSystems.getBy("P");
	}
	
	public SimpleRoadNetwork(VisumRoadNetwork network, VisumTransportSystem car) {
		super(car);
		System.out.println("Parsing nodes...");
		this.nodes = Collections.unmodifiableMap(parseNodes(network.nodes));
		System.out.println(this.nodes.size() + " nodes");

		System.out.println("Parsing edges...");
		this.edges = Collections.unmodifiableMap(parseEdges(network.links.links));
		System.out.println(this.edges.size() + " edges");

		System.out.println("Parsing zones...");
		this.zones = Collections.unmodifiableMap(parseZones(network));
		System.out.println(this.zones.size() + " zones");

		System.out.println("Parsing zone center nodes...");
		this.zoneCenterNodes = Collections.unmodifiableMap(parseZoneCenterNodes(network.zones));
		System.out.println("Parsing connectors...");
		this.connectorEdges = Collections.unmodifiableMap(parseConnectors(network.connectors));
	}

	public Map<Integer, Zone> zones() {
		return zones;
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



	protected List<SimpleEdge> makeEdges(
		VisumLink vLink,
		Map<Integer, Node> nodes
	) {

		List<SimpleEdge> result = new ArrayList<SimpleEdge>();

		VisumNode a_from = vLink.linkA.from;
		VisumNode a_to = vLink.linkA.to;
		VisumNode b_from = vLink.linkB.from;
		VisumNode b_to = vLink.linkB.to;

		assert a_from == b_to;
		assert a_to 	== b_from;

		Node n1 = nodes.get(a_from.id());
		Node n2 = nodes.get(a_to.id());

		assert n1 != null;
		assert n2 != null;

		SimpleEdge e1 = new SimpleEdge( vLink.id, n1, n2, vLink.linkA, carAllowed(vLink.linkA));
		SimpleEdge e2 = new SimpleEdge(-vLink.id, n2, n1, vLink.linkB, carAllowed(vLink.linkB));
		e1.twin = e2;
		e2.twin = e1;

		registerEdge(e1);
		registerEdge(e2);


		result.add(e1);
		result.add(e2);

		return result;
	}
	
	protected Map<Integer,Node> parseNodes(
		Map<Integer, VisumNode> nodes
	) {

		Map<Integer,Node> result = new HashMap<Integer,Node>();

		for (Integer id : nodes.keySet()) {
			VisumNode node = nodes.get(id);
			result.put(id, makeNode(node));
		}

		return result;
	}

	protected Map<Integer,Edge> parseEdges(
		Map<Integer, VisumLink> links
	) {

		Map<Integer,Edge> result = new HashMap<Integer,Edge>();

		for (Integer id : links.keySet()) {
			VisumLink link = links.get(id);

			List<SimpleEdge> edges = makeEdges(link, this.nodes);

			assert edges.size()>= 1 && edges.size()<= 2;

			result.put(id, edges.get(0));
			result.put(-id, edges.get(1));
		}

		return result;
	}

	protected Map<Integer,Zone> parseZones(
		VisumRoadNetwork visum
	) {

		Map<Integer, VisumZone> zones = visum.zones;

		Map<Integer,Zone> result = new HashMap<Integer,Zone>();

		for (Integer id : zones.keySet()) {
System.out.println("parsing zone " + id);
			VisumZone zone = zones.get(id);

			result.put(id, new Zone(this, visum, zone));
		}

		return result;
	}

	protected Map<Integer,SimpleNode> parseZoneCenterNodes(
		Map<Integer, VisumZone> zones
	) {


		Map<Integer,SimpleNode> result = new HashMap<Integer,SimpleNode>();

		for (Integer id : zones.keySet()) {
			VisumZone zone = zones.get(id);
			result.put(id, makeNode(id, zone.coord));
		}

		return result;
	}

	protected Map<Integer,List<Edge>> parseConnectors(
		 Map<Integer, List<VisumConnector>> connectors
	) {

		Map<Integer,List<Edge>> result = new HashMap<Integer,List<Edge>>();

		for (Integer id : connectors.keySet()) {
			List<VisumConnector> zoneConnectors = connectors.get(id);

			List<Edge> edges = makeConnector(zoneConnectors, this.nodes, this.zoneCenterNodes);

			result.put(id, edges);
		}

		return result;
	}

	public Zone zone(ZoneId id) {

		Integer zoneId = Integer.valueOf(id.getExternalId().substring(1));

		return zones.get(zoneId);
	}

	public static List<Edge> makeConnector(
		List<VisumConnector> vConnectors,
		Map<Integer, Node> nodes,
		Map<Integer, SimpleNode> zoneCenterNodes
	) {

		List<Edge> result = new ArrayList<Edge>();

		for (VisumConnector connector : vConnectors) {
	
			Node node = nodes.get(connector.node.id());
			SimpleNode zone = zoneCenterNodes.get(connector.zone.id);
	
			int id = connectorIds.nextId();
	
			if (connector.direction == VisumConnector.Direction.ORIGIN) {
				result.add(new SimpleEdge(id, node, zone, null, false));
			} else {
				result.add(new SimpleEdge(id, zone, node, null, false));
			}

		}

		return result;
	}


}
