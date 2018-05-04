package edu.kit.ifv.mobitopp.routing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import edu.kit.ifv.mobitopp.simulation.publictransport.VisumNodeFactory;
import edu.kit.ifv.mobitopp.visum.VisumConnector;
import edu.kit.ifv.mobitopp.visum.VisumLink;
import edu.kit.ifv.mobitopp.visum.VisumNode;
import edu.kit.ifv.mobitopp.visum.VisumOrientedLink;
import edu.kit.ifv.mobitopp.visum.VisumRoadNetwork;
import edu.kit.ifv.mobitopp.visum.VisumTransportSystem;
import edu.kit.ifv.mobitopp.visum.VisumZone;

public class GraphFromVisumNetwork implements Graph {

	final protected Map<Integer, Node> nodes;
	final public Map<Integer,Node> zones;
	final public Map<String, Link> links;
	final public List<Link> connectors;

	protected Node[] allNodes;

	public GraphFromVisumNetwork(VisumRoadNetwork visum) {
		this(visum, mivLinkValidation(carSystemFrom(visum)),
				mivConnectorValidation(carSystemFrom(visum)), LinkFromVisumLink::new,
				NodeFromVisumNode::new, NodeFromVisumZone::new);
	}
	
	GraphFromVisumNetwork(VisumRoadNetwork visum, VisumZoneNodeFactory zoneFactory) {
		this(visum, mivLinkValidation(carSystemFrom(visum)),
				mivConnectorValidation(carSystemFrom(visum)), LinkFromVisumLink::new,
				NodeFromVisumNode::new, zoneFactory);
	}
	
	public GraphFromVisumNetwork(
			VisumRoadNetwork network, Function<VisumOrientedLink, Boolean> linkValidation,
			Function<VisumConnector, Boolean> connectorValidation, VisumLinkFactory linkFactory,
			VisumNodeFactory nodeFactory) {
		this(network, linkValidation, connectorValidation, linkFactory, nodeFactory,
				NodeFromVisumZone::new);
	}
	
	protected static VisumTransportSystem carSystemFrom(VisumRoadNetwork visum) {
		return visum.getTransportSystem("P");
	}

	public GraphFromVisumNetwork(
			VisumRoadNetwork network, Function<VisumOrientedLink, Boolean> linkValidation,
			Function<VisumConnector, Boolean> connectorValidation, VisumLinkFactory linkFactory,
			VisumNodeFactory nodeFactory, VisumZoneNodeFactory zoneFactory) {
		Map<Integer, NodeFromVisumNode> tmpNodes = makeNodes(network.nodes, nodeFactory);
		this.links = makeValidLinks(network.links.links, tmpNodes, linkValidation, linkFactory);
		this.nodes = nodesFromLinks(this.links);

		this.zones = makeZones(network.zones, zoneFactory);

		this.connectors = makeValidConnectors(network.connectors, this.nodes, this.zones,
				connectorValidation);

		ArrayList<Node> tmp = new ArrayList<Node>();

		tmp.addAll(nodes.values());
		tmp.addAll(zones.values());

		allNodes = tmp.toArray(new Node[0]);
	}

	private static Function<VisumOrientedLink, Boolean> mivLinkValidation(
			VisumTransportSystem carSystem) {
		return link -> link.transportSystems.contains(carSystem) && link.attributes.capacityCar > 0.0f;
	}

	static Function<VisumConnector, Boolean> mivConnectorValidation(
			VisumTransportSystem carSystem) {
		return connector -> connector.transportSystems.contains(carSystem);
	}


	protected static Map<Integer, NodeFromVisumNode> makeNodes(Map<Integer, VisumNode> nodes, VisumNodeFactory factory) {

		HashMap<Integer, NodeFromVisumNode> data = new HashMap<Integer,NodeFromVisumNode>();

		for (VisumNode node: nodes.values()) {
			data.put(new Integer(node.id()),factory.create(node));
		}

		return data;
	}

	protected static Map<Integer, Node> nodesFromLinks(Map<String, Link> links) {
		HashMap<Integer,Node> data = new HashMap<Integer,Node>();
		for (Link link : links.values()) {
			data.put(Integer.valueOf(link.from().id().substring(1)),link.from());
			data.put(Integer.valueOf(link.to().id().substring(1)),link.to());
		}

		return data;
	}

	protected static Map<Integer, Node> makeZones(Map<Integer, VisumZone> zones, VisumZoneNodeFactory zoneFactory) {

		HashMap<Integer,Node> data = new HashMap<Integer,Node>();

		for (VisumZone zone: zones.values()) {
			data.put(zone.id, zoneFactory.create(zone));
		}

		return data;
	}


	protected static Map<String, Link> makeValidLinks(
		Map<Integer, VisumLink> links, 
		Map<Integer, NodeFromVisumNode> nodes, 
		Function<VisumOrientedLink, Boolean> linkValidation, 
		VisumLinkFactory linkFactory
	) {

		Map<String, Link> data = new HashMap<>();

		Map<NodeFromVisumNode,List<Link>> inLinks = new HashMap<NodeFromVisumNode,List<Link>>();
		Map<NodeFromVisumNode,List<Link>> outLinks = new HashMap<NodeFromVisumNode,List<Link>>();

		for (VisumLink link: links.values()) {

			VisumOrientedLink link1 = link.linkA;
			VisumOrientedLink link2 = link.linkB;

			if (linkValidation.apply(link1)) {
				Link mivLink = makeValidLink(link1, nodes, inLinks, outLinks, linkFactory);
				data.put(mivLink.id(), mivLink);
			}

			if (linkValidation.apply(link2)) {
				Link mivLink = makeValidLink(link2, nodes, inLinks, outLinks, linkFactory);
				data.put(mivLink.id(), mivLink);
			}
			
		}

		for (NodeFromVisumNode to : inLinks.keySet()) {

			Collection<Link> edges = inLinks.get(to);
			to.setIncomingLinks(edges);
		}

		for (NodeFromVisumNode from : outLinks.keySet()) {

			Collection<Link> edges = outLinks.get(from);
			from.setOutgoingLinks(edges);
		}


		return data;
	}

	protected static List<Link> makeValidConnectors(
		Map<Integer, List<VisumConnector>> connectors, 
		Map<Integer, ? extends Node> nodes,
		Map<Integer, Node> zones, 
		Function<VisumConnector, Boolean> connectorValidation
	) {

		ArrayList<Link> data = new ArrayList<Link>();

		Map<DefaultNode,List<Link>> inLinks = new HashMap<DefaultNode,List<Link>>();
		Map<DefaultNode,List<Link>> outLinks = new HashMap<DefaultNode,List<Link>>();

		for (List<VisumConnector> connectorsByZone: connectors.values()) {

			for (VisumConnector connector : connectorsByZone) {

				if (connectorValidation.apply(connector)) {

					NodeFromVisumNode node = (NodeFromVisumNode) nodes.get(connector.node.id());
					NodeFromVisumZone zone = (NodeFromVisumZone) zones.get(connector.zone.id);

					if (node != null) {

						if (!inLinks.containsKey(node)) {
							inLinks.put(node, new ArrayList<Link>());
						}
						if (!outLinks.containsKey(node)) {
							outLinks.put(node, new ArrayList<Link>());
						}
						if (!inLinks.containsKey(zone)) {
							inLinks.put(zone, new ArrayList<Link>());
						}
						if (!outLinks.containsKey(zone)) {
							outLinks.put(zone, new ArrayList<Link>());
						}
	
						if (connector.direction == VisumConnector.Direction.ORIGIN) {
	
								Link link = new LinkFromVisumConnector(connector, zone, node);	
								data.add(link);
	
								outLinks.get(zone).add(link);
								inLinks.get(node).add(link); // Don't add connector links to street network nodes
						} else {
								Link link = new LinkFromVisumConnector(connector, node, zone);	
								data.add(link);
	
								inLinks.get(zone).add(link);
								outLinks.get(node).add(link);  // Don't add connector links to street network nodes
						}

					}
				}

			}

		}

		for (DefaultNode to : inLinks.keySet()) {

			Collection<Link> edges = inLinks.get(to);

			edges.addAll(convertToLinkCollection(to.incomingEdges()));
			to.setIncomingLinks(edges);
		}

		for (DefaultNode from : outLinks.keySet()) {

			Collection<Link> edges = outLinks.get(from);

			edges.addAll(convertToLinkCollection(from.outgoingEdges()));
			from.setOutgoingLinks(edges);
		}

		return data;
	}

	protected static Collection<Link> convertToLinkCollection(Edge[] edges) {

		return Arrays.asList(Arrays.asList(edges).toArray(new Link[0]));
	}


	protected static Link makeValidLink(
		VisumOrientedLink link,
		Map<Integer, NodeFromVisumNode> nodes,
		Map<NodeFromVisumNode,List<Link>> inLinks,
		Map<NodeFromVisumNode,List<Link>> outLinks, 
		VisumLinkFactory linkFactory
	) {

		NodeFromVisumNode from = nodes.get(link.from.id());
		NodeFromVisumNode to 	= nodes.get(link.to.id());

		Link edge = linkFactory.create(link, from, to);


		if (!inLinks.containsKey(to)) {
			inLinks.put(to, new ArrayList<Link>());
		}
		if (!outLinks.containsKey(from)) {
			outLinks.put(from, new ArrayList<Link>());
		}

		inLinks.get(to).add(edge);
		outLinks.get(from).add(edge);

		return edge;
	}


	public Node getNodeForZone(int zoneId) {

		return this.zones.get(zoneId);
	}

	public Node[] nodes() {
		return allNodes;
	}


	public Edge[] outgoingEdges(Node node) {
		assert node instanceof DefaultNode;
		return node.outgoingEdges();
	}

	public Edge[] incomingEdges(Node node) {
		assert node instanceof DefaultNode;
		return node.incomingEdges();
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

		return ((DefaultLink)edge).travelTime();
	}


	public Map<Integer,Node> zones() {
		return this.zones;
	}

	public List<Link> links() {
		return new ArrayList<>(links.values());
	}

	public Node nodeFor(int id) {
		return nodes.get(id);
	}

	public Link linkFor(Integer edgeId) {
		if (edgeId == null) {
			return null;
		}
		return links.get(Math.abs(edgeId) + ":1");
	}

}
